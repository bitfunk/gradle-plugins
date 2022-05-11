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

package eu.bitfunk.gradle.plugin.quality.report

import eu.bitfunk.gradle.plugin.quality.report.ReportContract.Companion.EXTENSION_NAME
import eu.bitfunk.gradle.plugin.quality.report.ReportContract.Extension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.create
import org.sonarqube.gradle.SonarQubeExtension
import java.io.File

public class ReportPlugin : ReportContract.Plugin, Plugin<Project> {
    override fun apply(target: Project) {
        addPlugins(target)
        val extension = addExtension(target)
        configureReport(target, extension)
        configureTasks(target, extension)
    }

    override fun addPlugins(project: Project): Unit = with(project) {
        pluginManager.apply("org.sonarqube")
    }

    override fun addExtension(project: Project): Extension = with(project) {
        val extension = extensions.create(
            EXTENSION_NAME,
            ReportPluginExtension::class.java
        )

        extension.sonarProjectKey.convention("")
        extension.sonarOrganization.convention("")
        extension.coverageReportSourceDir.convention("$buildDir/reports/jacoco/testCodeCoverageReport")

        return extension
    }

    override fun configureReport(project: Project, extension: Extension): Unit = with(project) {
        sonarqube {
            properties {
                property("sonar.projectKey", extension.sonarProjectKey.get())
                property("sonar.organization", extension.sonarOrganization.get())
                property("sonar.host.url", "https://sonarcloud.io")

                property(
                    "sonar.sources",
                    collectProjects(
                        projectDir,
                        "src/main/kotlin"
                    ).map { "${it.relativeTo(projectDir)}/src/main/kotlin" })
                property(
                    "sonar.tests",
                    collectProjects(
                        projectDir,
                        "src/test/kotlin"
                    ).map { "${it.relativeTo(projectDir)}/src/test/kotlin" })
                property("sonar.sourceEncoding", "UTF-8")
                property("sonar.jacoco.reportPaths", "$buildDir/reports/jacoco/testCodeCoverageReport.xml")
            }
        }
    }

    override fun configureTasks(project: Project, extension: Extension): Unit = with(project) {
        tasks.create<Copy>("copyCoverageReports") {
            dependsOn("testCodeCoverageReport")

            group = "verification"

            from(extension.coverageReportSourceDir.get()) {
                include("*.xml")
            }

            into("$buildDir/reports/jacoco")

            includeEmptyDirs = false
        }

        tasks.named("sonarqube") {
            dependsOn("copyCoverageReports")
        }
    }

    private fun collectProjects(file: File, filter: String): List<File> {
        val projects = mutableListOf<File>()

        listOf(file)
            .extract(projects, filter)
            .extract(projects, filter)
            .extract(projects, filter)
            .toList()

        return projects
    }

    private fun List<File>.extract(targetList: MutableList<File>, filter: String): List<File> {
        return flatMap { it.listFiles().asSequence() }
            .filter { it.isDirectory && File(it, filter).exists() }
            .map { it.also { targetList.add(it) } }
    }

    private fun Project.sonarqube(action: Action<SonarQubeExtension>) {
        extensions.configure(SonarQubeExtension::class.java, action)
    }
}
