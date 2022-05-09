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

package eu.bitfunk.gradle.plugin.quality.code.analysis

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.DetektReports
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
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.TaskContainer
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class CodeAnalysisPluginTest {

    private lateinit var project: Project

    private lateinit var testSubject: CodeAnalysisPlugin

    @BeforeEach
    fun setup() {
        project = mockk()

        testSubject = CodeAnalysisPlugin()
    }

    @Test
    fun plugin_implements_contract() {
        assertInstanceOf(
            CodeAnalysisContract.Plugin::class.java,
            testSubject
        )
    }

    @Test
    fun plugin_is_gradle_plugin() {
        assertInstanceOf(
            Plugin::class.java,
            testSubject
        )
    }

    @Test
    fun `GIVEN project WHEN appPlugins() THEN plugins added`() {
        // GIVEN
        val pluginManager: PluginManager = mockk(relaxed = true)
        every { project.pluginManager } returns pluginManager

        // WHEN
        testSubject.addPlugins(project)

        // THEN
        verify {
            pluginManager.apply("io.gitlab.arturbosch.detekt")
        }

        confirmVerified(pluginManager)
    }

    @Test
    fun `GIVEN project WHEN configureAnalysis() THEN analysis configured`() {
        // GIVEN
        val extensionContainer: ExtensionContainer = mockk()
        val versionCatalogsExtension: VersionCatalogsExtension = mockk()
        val versionCatalog: VersionCatalog = mockk()
        val requiredVersion = "requiredVersion"
        val detektExtension: DetektExtension = mockk(relaxed = true)
        val rootDir: File = mockk()
        val projectFile: File = mockk()
        val projectFiles: ConfigurableFileCollection = mockk()
        val rootProject: Project = mockk()
        val rootProjectFiles: ConfigurableFileCollection = mockk()
        val rootProjectFile: File = mockk()
        every { project.extensions } returns extensionContainer
        every { extensionContainer.getByType(VersionCatalogsExtension::class.java) } returns versionCatalogsExtension
        every { versionCatalogsExtension.named(any()) } returns versionCatalog
        every { versionCatalog.findVersion(any()).get().requiredVersion } returns requiredVersion
        every { extensionContainer.configure(DetektExtension::class.java, any()) } answers {
            secondArg<Action<DetektExtension>>().execute(detektExtension)
        }
        every { project.rootDir } returns rootDir
        every { project.file(rootDir) } returns projectFile
        every { project.files(projectFile) } returns projectFiles
        every { project.rootProject } returns rootProject
        every { rootProject.files(any()) } returns rootProjectFiles
        every { rootProject.file(any()) } returns rootProjectFile

        // WHEN
        testSubject.configureAnalysis(project)

        // THEN
        verifyAll {
            extensionContainer.getByType(VersionCatalogsExtension::class.java)
            extensionContainer.configure(DetektExtension::class.java, any())

            detektExtension.toolVersion = requiredVersion
            detektExtension.parallel = true

            detektExtension.source = projectFiles

            detektExtension.config = rootProjectFiles
            rootProject.files("config/detekt/config.xml")

            detektExtension.baseline = rootProjectFile
            rootProject.file("config/detekt/baseline.yml")
        }

        confirmVerified(extensionContainer, detektExtension, projectFiles, rootProjectFiles, rootProjectFile)
    }

    @Test
    fun `GIVEN project WHEN configureTasks() THEN tasks configured`() {
        // GIVEN
        val taskContainer: TaskContainer = mockk()
        val detektTask: Detekt = mockk(relaxed = true)
        val detektReports: DetektReports = mockk(relaxed = true)
        val detektCreateBaselineTask: DetektCreateBaselineTask = mockk(relaxed = true)
        every { project.tasks } returns taskContainer
        every { taskContainer.withType(Detekt::class.java).configureEach(any()) } answers {
            firstArg<Action<Detekt>>().execute(detektTask)
        }
        every { detektTask.reports(any()) } answers {
            firstArg<Action<DetektReports>>().execute(detektReports)
        }
        every { taskContainer.withType(DetektCreateBaselineTask::class.java).configureEach(any()) } answers {
            firstArg<Action<DetektCreateBaselineTask>>().execute(detektCreateBaselineTask)
        }

        // WHEN
        testSubject.configureAnalysisTasks(project)

        // THEN
        verifyAll {
            taskContainer.withType(Detekt::class.java).configureEach(any())

            detektTask.jvmTarget = "11"
            detektTask.exclude(*DETEKT_EXCLUDES)
            detektTask.reports(any())
            detektReports.xml.required.set(true)
            detektReports.html.required.set(true)

            taskContainer.withType(DetektCreateBaselineTask::class.java).configureEach(any())

            detektCreateBaselineTask.exclude(*DETEKT_BASELINE_EXCLUDED)
        }

        confirmVerified(
            taskContainer,
            detektTask,
            detektReports,
            detektCreateBaselineTask
        )
    }

    @Test
    fun `GIVEN project WHEN apply() THEN all configured`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)
        val spyTestSubject = spyk(testSubject)
        val versionCatalog: VersionCatalog = mockk()
        every { project.extensions.getByType(VersionCatalogsExtension::class.java).named(any()) } returns versionCatalog
        every { versionCatalog.findVersion(any()).get().requiredVersion } returns "requiredVersion"

        // WHEN
        spyTestSubject.apply(project)

        // THEN
        verifyOrder {
            spyTestSubject.apply(project)
            spyTestSubject.addPlugins(project)
            spyTestSubject.configureAnalysis(project)
            spyTestSubject.configureAnalysisTasks(project)
        }

        confirmVerified(
            spyTestSubject
        )
    }

    private companion object {
        val DETEKT_EXCLUDES = arrayOf(
            "**/.gradle/**",
            "**/.idea/**",
            "**/build/**",
            ".github/**",
            "gradle/**",
        )

        val DETEKT_BASELINE_EXCLUDED = arrayOf(
            "**/.gradle/**",
            "**/.idea/**",
            "**/build/**",
            "**/gradle/wrapper/**",
            ".github/**",
            "assets/**",
            "app-ios/**",
            "docs/**",
            "gradle/**",
            "**/*.adoc",
            "**/gradlew",
            "**/LICENSE",
            "**/.java-version",
            "**/gradlew.bat",
            "**/*.png",
            "**/*.properties",
            "**/*.pro",
            "**/*.sq",
            "**/*.xml",
            "**/*.yml",
        )
    }
}
