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
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyAll
import io.mockk.verifyOrder
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.Copy
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.sonarqube.gradle.SonarQubeExtension
import org.sonarqube.gradle.SonarQubeProperties
import java.io.File

class ReportPluginTest {

    private lateinit var testSubject: ReportPlugin

    @BeforeEach
    fun setup() {
        testSubject = ReportPlugin()
    }

    @Test
    fun `implements contract`() {
        assertInstanceOf(
            ReportContract.Plugin::class.java,
            testSubject
        )
    }

    @Test
    fun `implements Gradle Plugin`() {
        assertInstanceOf(
            Plugin::class.java,
            testSubject
        )
    }

    @Test
    fun `GIVEN project WHEN addPlugins() THEN plugins added`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)

        // WHEN
        testSubject.addPlugins(project)

        // THEN
        verify { project.pluginManager.apply("org.sonarqube") }

        confirmVerified(project)
    }

    @Test
    fun `GIVEN project WHEN addExtension() THEN extension added`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)
        val extension: ReportPluginExtension = mockk(relaxed = true)
        val buildDir: File = File("buildDir")
        every { project.extensions.create("reportConfig", ReportPluginExtension::class.java) } returns extension
        every { project.buildDir } returns buildDir

        // WHEN
        testSubject.addExtension(project)

        // THEN
        verifyAll {
            project.extensions.create("reportConfig", ReportPluginExtension::class.java)

            project.buildDir

            extension.sonarProjectKey.convention("")
            extension.sonarOrganization.convention("")
            extension.coverageReportSourceDir.convention("buildDir/reports/jacoco/testCodeCoverageReport")
        }

        confirmVerified(project, extension)
    }

    @Test
    fun `GIVEN project WHEN configureReport() THEN report configured`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)
        val extension: ReportPluginExtension = mockk(relaxed = true)
        val collector: Collector = mockk(relaxed = true)
        val sonarQubeExtension: SonarQubeExtension = mockk(relaxed = true)
        val sonarQubeProperties: SonarQubeProperties = mockk(relaxed = true)
        every { project.extensions.configure(SonarQubeExtension::class.java, any()) } answers {
            secondArg<Action<SonarQubeExtension>>().execute(sonarQubeExtension)
        }
        every { sonarQubeExtension.properties(any()) } answers {
            firstArg<Action<SonarQubeProperties>>().execute(sonarQubeProperties)
        }
        every { extension.sonarProjectKey.get() } returns "sonarProjectKey"
        every { extension.sonarOrganization.get() } returns "sonarOrganization"
        val projectDir: File = mockk()
        every { project.projectDir } returns projectDir
        every { project.buildDir } returns File("build")
        val srcProjects = listOf("srcProjects")
        val testProjects = listOf("testProjects")
        every { collector.collectProjects(projectDir, "src/main/kotlin") } returns srcProjects
        every { collector.collectProjects(projectDir, "src/test/kotlin") } returns testProjects

        // WHEN
        testSubject.configureReport(project, extension, collector)

        // THEN
        verifyAll {
            project.extensions.configure(SonarQubeExtension::class.java, any())

            sonarQubeExtension.properties(any())
            project.projectDir
            project.buildDir

            extension.sonarProjectKey
            extension.sonarOrganization

            sonarQubeProperties.property("sonar.projectKey", "sonarProjectKey")
            sonarQubeProperties.property("sonar.organization", "sonarOrganization")
            sonarQubeProperties.property("sonar.host.url", "https://sonarcloud.io")

            collector.collectProjects(projectDir, "src/main/kotlin")
            sonarQubeProperties.property("sonar.sources", srcProjects)
            collector.collectProjects(projectDir, "src/test/kotlin")
            sonarQubeProperties.property("sonar.tests", testProjects)

            sonarQubeProperties.property("sonar.sourceEncoding", "UTF-8")
            sonarQubeProperties.property("sonar.jacoco.reportPaths", "build/reports/jacoco/testCodeCoverageReport.xml")
        }

        confirmVerified(project, extension, collector, sonarQubeExtension, sonarQubeProperties)
    }

    @Test
    fun `GIVEN project WHEN configureTasks() THEN tasks configured`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)
        val extension: ReportPluginExtension = mockk(relaxed = true)
        val copyTask: Copy = mockk(relaxed = true)
        val copyTaskSpec: CopySpec = mockk(relaxed = true)
        val sonarqubeTask: Task = mockk(relaxed = true)
        every { project.tasks.create("copyCoverageReports", Copy::class.java, any()) } answers {
            thirdArg<Action<Copy>>().execute(copyTask)
            copyTask
        }
        every { extension.coverageReportSourceDir.get() } returns "coverageReportSourceDir"
        every { project.buildDir } returns File("buildDir")
        every { copyTask.from(any(), any<Action<CopySpec>>()) } answers {
            secondArg<Action<CopySpec>>().execute(copyTaskSpec)
            copyTask
        }
        every { project.tasks.named("sonarqube", any()) } answers {
            secondArg<Action<Task>>().execute(sonarqubeTask)
            mockk()
        }

        // WHEN
        testSubject.configureTasks(project, extension)

        // THEN
        verify {
            project.tasks.create("copyCoverageReports", Copy::class.java, any())
            project.buildDir

            extension.coverageReportSourceDir

            copyTask.dependsOn("testCodeCoverageReport")
            copyTask.group = "verification"
            copyTask.from("coverageReportSourceDir", any<Action<CopySpec>>())
            copyTask.into("buildDir/reports/jacoco")
            copyTask.includeEmptyDirs = false

            copyTaskSpec.include("*.xml")

            project.tasks.named("sonarqube", any())
            sonarqubeTask.dependsOn("copyCoverageReports")
        }

        confirmVerified(project, extension, copyTask, copyTaskSpec, sonarqubeTask)
    }

    @Test
    fun `GIVEN project WHEN apply() THEN all configured`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)
        val spyTestSubject = spyk(testSubject)
        val extension: ReportPluginExtension = mockk(relaxed = true)
        every { project.extensions.create(any(), ReportPluginExtension::class.java) } returns extension

        // WHEN
        spyTestSubject.apply(project)

        // THEN
        verifyOrder {
            spyTestSubject.apply(project)
            spyTestSubject.addPlugins(project)
            spyTestSubject.addExtension(project)
            spyTestSubject.configureReport(project, extension, any())
            spyTestSubject.configureTasks(project, extension)
        }

        confirmVerified(spyTestSubject)
    }
}
