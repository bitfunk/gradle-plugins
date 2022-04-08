/*
 * ISC License
 *
 * Copyright (c) 2022. Wolf-Martell Montwé (bitfunk)
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
 * LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 * OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 * PERFORMANCE OF THIS SOFTWARE.
 */

package eu.bitfunk.gradle.version.catalog

import eu.bitfunk.gradle.version.catalog.VersionCatalogAccessorContract.Companion.EXTENSION_NAME
import eu.bitfunk.gradle.version.catalog.VersionCatalogAccessorContract.Companion.TASK_NAME_COPY_SOURCE
import eu.bitfunk.gradle.version.catalog.VersionCatalogAccessorContract.Companion.TASK_NAME_GENERATE
import eu.bitfunk.gradle.version.catalog.VersionCatalogAccessorContract.Companion.TASK_NAME_GENERATE_SOURCE
import eu.bitfunk.gradle.version.catalog.VersionCatalogAccessorContract.Extension
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.register
import org.gradle.util.GradleVersion

public class VersionCatalogAccessorPlugin : Plugin<Project>, VersionCatalogAccessorContract.Plugin {

    override fun apply(target: Project) {
        if (GradleVersion.current() < GradleVersion.version("7.2")) {
            throw GradleException("This plugin requires Gradle 7.2 or later")
        }

        if (target != target.rootProject) {
            throw GradleException("This plugin should be applied to root project only")
        }

        if (!target.pluginManager.hasPlugin("java-gradle-plugin")) {
            throw GradleException("The VersionCatalogAccessorPlugin requires the `java-gradle-plugin` to work.")
        }

        val extension = addExtension(target)
        addSourceGeneratorTask(target, extension)
        addSourceCopyTask(target, extension)
        addGeneratorTask(target)
        configureSourceSet(target)
    }

    override fun addExtension(project: Project): Extension {
        val extension = project.extensions.create(
            EXTENSION_NAME,
            VersionCatalogAccessorPluginExtension::class.java
        )

        extension.catalogSourceFolder.convention("gradle/")
        extension.catalogNames.convention(listOf("libs"))
        extension.packageName.convention("")

        return extension
    }

    override fun addSourceGeneratorTask(
        project: Project,
        extension: Extension
    ): VersionCatalogAccessorSourceGeneratorTask {
        val taskProvider = project.tasks.register<VersionCatalogAccessorSourceGeneratorTask>(TASK_NAME_GENERATE_SOURCE) {
            catalogSourceFolder.set(extension.catalogSourceFolder)
            catalogNames.set(extension.catalogNames)
            packageName.set(extension.packageName)
        }
        return taskProvider.get()
    }

    override fun addSourceCopyTask(project: Project, extension: Extension): VersionCatalogAccessorSourceCopyTask {
        val taskProvider = project.tasks.register<VersionCatalogAccessorSourceCopyTask>(TASK_NAME_COPY_SOURCE)
        return taskProvider.get()
    }

    override fun addGeneratorTask(project: Project): Task {
        val taskProvider = project.tasks.register<Task>(TASK_NAME_GENERATE) {
            dependsOn(TASK_NAME_GENERATE_SOURCE, TASK_NAME_COPY_SOURCE)
        }
        return taskProvider.get()
    }

    override fun configureSourceSet(project: Project) {
        project.sourceSets {
            named("main").configure {
                java.srcDir(project.layout.buildDirectory.dir(OUTPUT_PATH).get())
            }
        }
    }

    private fun Project.sourceSets(configure: Action<SourceSetContainer>): Unit =
        (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("sourceSets", configure)

    private companion object {
        private const val OUTPUT_PATH = "generated/versionCatalogAccessor/src/main/kotlin"
    }
}
