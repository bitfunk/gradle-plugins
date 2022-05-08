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

package eu.bitfunk.gradle.plugin.development.version.catalog.accessor

import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.VersionCatalogAccessorContract.Companion.EXTENSION_NAME
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.VersionCatalogAccessorContract.Companion.TASK_NAME_COPY_SOURCE
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.VersionCatalogAccessorContract.Companion.TASK_NAME_GENERATE
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.VersionCatalogAccessorContract.Companion.TASK_NAME_GENERATE_SOURCE
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.VersionCatalogAccessorContract.Extension
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
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
        addSourceCopyTask(target)
        addGeneratorTask(target)
        configureSourceSet(target)
    }

    override fun addExtension(project: Project): Extension = with(project) {
        val extension = extensions.create(
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
    ): VersionCatalogAccessorSourceGeneratorTask = with(project) {
        val taskProvider = tasks.register<VersionCatalogAccessorSourceGeneratorTask>(TASK_NAME_GENERATE_SOURCE) {
            catalogSourceFolder.set(extension.catalogSourceFolder)
            catalogNames.set(extension.catalogNames)
            packageName.set(extension.packageName)
        }
        return taskProvider.get()
    }

    override fun addSourceCopyTask(project: Project): Unit = with(project) {
        tasks.register<VersionCatalogAccessorSourceCopyTask>(TASK_NAME_COPY_SOURCE)
    }

    override fun addGeneratorTask(project: Project): Unit = with(project) {
        tasks.register<Task>(TASK_NAME_GENERATE) {
            dependsOn(TASK_NAME_GENERATE_SOURCE, TASK_NAME_COPY_SOURCE)
        }

        tasks.named("assemble") {
            dependsOn(TASK_NAME_GENERATE)
        }
    }

    override fun configureSourceSet(project: Project): Unit = with(project) {
        sourceSets {
            named("main").configure {
                java.srcDir(project.layout.buildDirectory.dir(OUTPUT_PATH).get())
            }
        }
    }

    override fun configureCodeCoverage(project: Project): Unit = with(project) {
        // if (pluginManager.hasPlugin("org.gradle.jacoco")) {
        tasks.named("test", Test::class.java) {
            extensions.configure(JacocoTaskExtension::class.java) {
                excludes = coverageExcludes
            }
        }

        tasks.named<JacocoReport>("jacocoTestReport") {
            excludeClassFilesFromCoverage(project, classDirectories)
        }

        tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
            excludeClassFilesFromCoverage(project, classDirectories)
        }
        // }
    }

    private fun excludeClassFilesFromCoverage(project: Project, classDirectories: ConfigurableFileCollection) {
        classDirectories.setFrom(
            project.files(
                classDirectories.files.map {
                    project.fileTree(it) {
                        exclude(coverageExcludes)
                    }
                }
            )
        )
    }

    private fun Project.sourceSets(configure: Action<SourceSetContainer>): Unit =
        (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("sourceSets", configure)

    private companion object {
        private const val OUTPUT_PATH = "generated/versionCatalogAccessor/src/main/kotlin"

        private val coverageExcludes = listOf("**/generated/**")
    }
}
