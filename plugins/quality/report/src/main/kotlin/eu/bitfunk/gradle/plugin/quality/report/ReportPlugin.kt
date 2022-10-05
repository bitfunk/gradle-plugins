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

import eu.bitfunk.gradle.plugin.quality.report.ReportContract.Collector
import eu.bitfunk.gradle.plugin.quality.report.ReportContract.Companion.EXTENSION_NAME
import eu.bitfunk.gradle.plugin.quality.report.ReportContract.Extension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.create
import org.sonarqube.gradle.SonarQubeExtension

public class ReportPlugin : ReportContract.Plugin, Plugin<Project> {
    override fun apply(target: Project) {
        val collector = ProjectCollector()

        addPlugins(target)
        val extension = addExtension(target)
        configureReport(target, extension, collector)
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
        extension.coverageReportSourceDirs.convention(listOf("$buildDir/reports/jacoco/testCodeCoverageReport"))

        return extension
    }

    override fun configureReport(
        project: Project,
        extension: Extension,
        collector: Collector
    ): Unit = with(project) {
        val projectsWithSrc = collector.collectProjects(projectDir, "src/main/kotlin")
        val projectsWithTests = collector.collectProjects(projectDir, "src/test/kotlin")
        val sourceFolderCount = extension.coverageReportSourceDirs.get().size
        val reportPaths = mutableListOf<String>()
        repeat(sourceFolderCount) { index ->
            reportPaths.add("$buildDir/reports/jacoco/testCodeCoverageReport-${index + 1}.xml")
        }

        sonarqube {
            properties {
                property("sonar.projectKey", extension.sonarProjectKey.get())
                property("sonar.organization", extension.sonarOrganization.get())
                property("sonar.host.url", "https://sonarcloud.io")

                property("sonar.sources", projectsWithSrc)
                property("sonar.tests", projectsWithTests)
                property("sonar.sourceEncoding", "UTF-8")
                property("sonar.jacoco.reportPaths", reportPaths)
            }
        }
    }

    override fun configureTasks(project: Project, extension: Extension): Unit = with(project) {
        tasks.create<Copy>("copyCoverageReports") {
            dependsOn("testCodeCoverageReport")

            group = "verification"

            from(extension.coverageReportSourceDirs) {
                include("*.xml")
            }

            into("$buildDir/reports/jacoco")

            var count = 0

            rename { fileName ->
                count++
                fileName.replace(".xml", "-$count.xml")
            }

            includeEmptyDirs = false
        }

        tasks.named("sonarqube") {
            dependsOn("copyCoverageReports")
        }
    }

    private fun Project.sonarqube(action: Action<SonarQubeExtension>) {
        extensions.configure(SonarQubeExtension::class.java, action)
    }
}
