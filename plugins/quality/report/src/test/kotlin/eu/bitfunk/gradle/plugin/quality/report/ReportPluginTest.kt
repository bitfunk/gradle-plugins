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
import eu.bitfunk.gradle.plugin.quality.report.intern.FileNameTransformer
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
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Copy
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.sonarqube.gradle.SonarExtension
import org.sonarqube.gradle.SonarProperties
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
            testSubject,
        )
    }

    @Test
    fun `implements Gradle Plugin`() {
        assertInstanceOf(
            Plugin::class.java,
            testSubject,
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
            extension.coverageReportSourceDirs.convention(listOf("buildDir/reports/jacoco/testCodeCoverageReport"))
        }

        confirmVerified(project, extension)
    }

    @Test
    fun `GIVEN project WHEN configureReport() THEN report configured`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)
        val extension: ReportPluginExtension = mockk(relaxed = true)
        val collector: Collector = mockk(relaxed = true)
        val sonarExtension: SonarExtension = mockk(relaxed = true)
        val sonarProperties: SonarProperties = mockk(relaxed = true)
        every { project.extensions.configure(SonarExtension::class.java, any()) } answers {
            secondArg<Action<SonarExtension>>().execute(sonarExtension)
        }
        every { sonarExtension.properties(any()) } answers {
            firstArg<Action<SonarProperties>>().execute(sonarProperties)
        }
        every { extension.sonarProjectKey.get() } returns "sonarProjectKey"
        every { extension.sonarOrganization.get() } returns "sonarOrganization"
        every { extension.coverageReportSourceDirs.get() } returns listOf(
            "coverageReportSourceDirs1",
            "coverageReportSourceDirs2",
        )
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
            project.extensions.configure(SonarExtension::class.java, any())

            sonarExtension.properties(any())
            project.projectDir
            project.buildDir

            extension.sonarProjectKey
            extension.sonarOrganization
            extension.coverageReportSourceDirs

            sonarProperties.property("sonar.projectKey", "sonarProjectKey")
            sonarProperties.property("sonar.organization", "sonarOrganization")
            sonarProperties.property("sonar.host.url", "https://sonarcloud.io")

            collector.collectProjects(projectDir, "src/main/kotlin")
            sonarProperties.property("sonar.sources", srcProjects)
            collector.collectProjects(projectDir, "src/test/kotlin")
            sonarProperties.property("sonar.tests", testProjects)

            sonarProperties.property("sonar.sourceEncoding", "UTF-8")
            sonarProperties.property(
                "sonar.coverage.jacoco.xmlReportPaths",
                "build/reports/jacoco/testCodeCoverageReport-1.xml,build/reports/jacoco/testCodeCoverageReport-2.xml",
            )
        }

        confirmVerified(project, extension, collector, sonarExtension, sonarProperties)
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
        val coverageSrcsDirProperty: ListProperty<String> = mockk(relaxed = true)
        every { extension.coverageReportSourceDirs } returns coverageSrcsDirProperty
        every { project.buildDir } returns File("buildDir")
        every { copyTask.from(any(), any<Action<CopySpec>>()) } answers {
            secondArg<Action<CopySpec>>().execute(copyTaskSpec)
            copyTask
        }
        every { copyTask.rename(any<FileNameTransformer>()) } answers {
            copyTask
        }
        every { project.tasks.named("sonar", any()) } answers {
            secondArg<Action<Task>>().execute(sonarqubeTask)
            mockk()
        }

        // WHEN
        testSubject.configureTasks(project, extension)

        // THEN
        verify {
            project.tasks.create("copyCoverageReports", Copy::class.java, any())
            project.buildDir

            extension.coverageReportSourceDirs

            copyTask.dependsOn("testCodeCoverageReport")
            copyTask.group = "verification"
            copyTask.from(coverageSrcsDirProperty, any<Action<CopySpec>>())
            copyTask.into("buildDir/reports/jacoco")
            copyTask.rename(any<FileNameTransformer>())
            copyTask.includeEmptyDirs = false

            copyTaskSpec.include("*.xml")

            project.tasks.named("sonar", any())
            sonarqubeTask.dependsOn("copyCoverageReports")
        }

        confirmVerified(project, extension, copyTask, coverageSrcsDirProperty, copyTaskSpec, sonarqubeTask)
    }

    @Test
    fun `GIVEN project WHEN apply() THEN all configured`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)
        val spyTestSubject = spyk(testSubject)
        val extension: ReportPluginExtension = mockk(relaxed = true)
        every { project.extensions.create(any(), ReportPluginExtension::class.java) } returns extension
        every { extension.coverageReportSourceDirs.get() } returns listOf("coverageReportSourceDirs")

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
