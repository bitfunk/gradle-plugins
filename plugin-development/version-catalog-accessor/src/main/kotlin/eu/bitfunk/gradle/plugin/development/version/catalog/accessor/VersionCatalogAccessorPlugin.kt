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
        checkPreconditions(target)
        val extension = addExtension(target)
        addSourceGeneratorTask(target, extension)
        addGeneratorTask(target)
        configureSourceSet(target)
        configureCodeCoverage(target)
    }

    override fun checkPreconditions(project: Project) {
        if (GradleVersion.current() < GradleVersion.version("7.2")) {
            throw GradleException("This plugin requires Gradle 7.2 or later")
        }

        if (project != project.rootProject) {
            throw GradleException("This plugin should be applied to root project only")
        }

        if (!project.pluginManager.hasPlugin("org.gradle.java-gradle-plugin")) {
            throw GradleException("The VersionCatalogAccessorPlugin requires `java-gradle-plugin` to work.")
        }

        if (!project.pluginManager.hasPlugin("org.gradle.kotlin.kotlin-dsl")) {
            throw GradleException("The VersionCatalogAccessorPlugin requires `kotlin-dsl` to work.")
        }
    }

    override fun addExtension(project: Project): Extension = with(project) {
        val extension = extensions.create(
            EXTENSION_NAME,
            VersionCatalogAccessorPluginExtension::class.java,
        )

        extension.catalogSourceFolder.convention("gradle/")
        extension.catalogNames.convention(listOf("libs"))
        extension.packageName.convention("")

        return extension
    }

    override fun addSourceGeneratorTask(
        project: Project,
        extension: Extension,
    ): VersionCatalogAccessorSourceGeneratorTask = with(project) {
        val taskProvider = tasks.register<VersionCatalogAccessorSourceGeneratorTask>(TASK_NAME_GENERATE_SOURCE) {
            catalogSourceFolder.set(extension.catalogSourceFolder)
            catalogNames.set(extension.catalogNames)
            packageName.set(extension.packageName)
        }
        return taskProvider.get()
    }

    override fun addGeneratorTask(project: Project): Unit = with(project) {
        tasks.register<Task>(TASK_NAME_GENERATE) {
            dependsOn(TASK_NAME_GENERATE_SOURCE)
        }

        tasks.named("compileKotlin") {
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
        if (pluginManager.hasPlugin("org.gradle.jacoco")) {
            tasks.withType(Test::class.java).configureEach {
                extensions.configure(JacocoTaskExtension::class.java) {
                    excludes = coverageExcludes
                }
            }

            tasks.withType(JacocoReport::class.java).configureEach {
                excludeClassFilesFromCoverage(project, classDirectories)
            }

            tasks.withType(JacocoCoverageVerification::class.java).configureEach {
                excludeClassFilesFromCoverage(project, classDirectories)
            }
        }
    }

    private fun excludeClassFilesFromCoverage(project: Project, classDirectories: ConfigurableFileCollection) {
        classDirectories.setFrom(
            project.files(
                classDirectories.files.map {
                    project.fileTree(it) {
                        exclude(coverageExcludes)
                    }
                },
            ),
        )
    }

    private fun Project.sourceSets(configure: Action<SourceSetContainer>): Unit =
        (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("sourceSets", configure)

    private companion object {
        private const val OUTPUT_PATH = "generated/versionCatalogAccessor/src/main/kotlin"

        private val coverageExcludes = listOf("**/generated")
    }
}
