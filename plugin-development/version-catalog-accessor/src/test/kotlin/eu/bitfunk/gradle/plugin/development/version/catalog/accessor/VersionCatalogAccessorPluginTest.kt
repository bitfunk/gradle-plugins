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

import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.fake.FakeProject
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import io.mockk.verifyAll
import io.mockk.verifyOrder
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.util.GradleVersion
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import org.gradle.api.tasks.testing.Test as TestTask

class VersionCatalogAccessorPluginTest {

    private lateinit var project: Project

    private lateinit var testSubject: VersionCatalogAccessorPlugin

    @BeforeEach
    fun setup() {
        project = mockk()

        testSubject = VersionCatalogAccessorPlugin()
    }

    @Test
    fun `implements contract`() {
        assertInstanceOf(
            VersionCatalogAccessorContract.Plugin::class.java,
            testSubject
        )
    }

    @Test
    fun `implements plugin`() {
        assertInstanceOf(
            Plugin::class.java,
            testSubject
        )
    }

    @Test
    fun `GIVEN Gradle version 7_1 WHEN checkPreconditions() THEN throw GradleException`() {
        // GIVEN
        mockkStatic(GradleVersion::class)
        every { GradleVersion.current() } returns GradleVersion.version("7.1")

        // WHEN/THEN
        assertThrowsExactly(
            GradleException::class.java,
            { testSubject.checkPreconditions(project) },
            "This plugin requires Gradle 7.2 or later"
        )

        unmockkAll() // mockStatic!!
    }

    @Test
    fun `GIVEN rootProject different to project WHEN checkPreconditions() THEN throw GradleException`() {
        // GIVEN
        val newProject: Project = mockk()
        every { project.rootProject } returns newProject

        // WHEN/THEN
        assertThrowsExactly(
            GradleException::class.java,
            { testSubject.checkPreconditions(project) },
            "This plugin should be applied to root project only"
        )
    }

    @Test
    fun `GIVEN java-gradle-plugin missing WHEN checkPreconditions() THEN throw GradleException`() {
        // GIVEN
        every { project.rootProject } returns project
        val pluginManager: PluginManager = mockk()
        every { project.pluginManager } returns pluginManager
        every { pluginManager.hasPlugin("org.gradle.java-gradle-plugin") } returns false

        // WHEN/THEN
        assertThrowsExactly(
            GradleException::class.java,
            { testSubject.checkPreconditions(project) },
            "The VersionCatalogAccessorPlugin requires `java-gradle-plugin` to work."
        )
    }

    @Test
    fun `GIVEN kotlin-dsl missing WHEN checkPreconditions() THEN throw GradleException`() {
        // GIVEN
        every { project.rootProject } returns project
        val pluginManager: PluginManager = mockk()
        every { project.pluginManager } returns pluginManager
        every { pluginManager.hasPlugin("org.gradle.java-gradle-plugin") } returns true
        every { pluginManager.hasPlugin("org.gradle.kotlin.kotlin-dsl") } returns false

        // WHEN/THEN
        assertThrowsExactly(
            GradleException::class.java,
            { testSubject.checkPreconditions(project) },
            "The VersionCatalogAccessorPlugin requires `kotlin-dsl` to work."
        )
    }

    @Test
    fun `GIVEN all required WHEN checkPreconditions() THEN success`() {
        // GIVEN
        every { project.rootProject } returns project
        val pluginManager: PluginManager = mockk()
        every { project.pluginManager } returns pluginManager
        every { pluginManager.hasPlugin("org.gradle.java-gradle-plugin") } returns true
        every { pluginManager.hasPlugin("org.gradle.kotlin.kotlin-dsl") } returns true

        // WHEN/THEN
        assertDoesNotThrow { testSubject.checkPreconditions(project) }
    }

    @Test
    fun `GIVEN project with plugin WHEN addExtension() THEN extension is added to project with defaults`() {
        // GIVEN
        every { project.rootProject } returns project
        val extensions: ExtensionContainer = mockk()
        every { project.extensions } returns extensions
        val extension: VersionCatalogAccessorPluginExtension = mockk(relaxed = true)
        every {
            extensions.create(any(), VersionCatalogAccessorPluginExtension::class.java)
        } returns extension

        // WHEN
        val result = testSubject.addExtension(project)

        // THEN
        assertEquals(
            extension,
            result
        )

        verify { extensions.create("versionCatalogAccessor", VersionCatalogAccessorPluginExtension::class.java) }
        verify { extension.catalogSourceFolder.convention("gradle/") }
        verify { extension.catalogNames.convention(listOf("libs")) }
        verify { extension.packageName.convention("") }
    }

    @Test
    fun `GIVEN project and extension WHEN addSourceGeneratorTask() THEN task is registered and configured`() {
        // GIVEN
        val project = ProjectBuilder.builder().build()
        val extension = spyk(testSubject.addExtension(project))

        // WHEN
        val task = testSubject.addSourceGeneratorTask(project, extension)

        // THEN
        assertEquals("generateVersionCatalogAccessorSource", task.name)
        assertEquals("gradle/", task.catalogSourceFolder.get())
        assertEquals(listOf("libs"), task.catalogNames.get())
        assertEquals("", task.packageName.get())
        verify { extension.catalogSourceFolder }
        verify { extension.catalogNames }
        verify { extension.packageName }
    }

    @Test
    fun `GIVEN project WHEN addGeneratorTask() THEN task is registered and configured`() {
        // GIVEN
        val taskContainer: TaskContainer = mockk()
        val generatorTaskProvider: TaskProvider<Task> = mockk()
        val generatorTask: Task = mockk(relaxed = true)
        val compileKotlinTaskProvider: TaskProvider<Task> = mockk()
        val compileKotlinTask: Task = mockk(relaxed = true)
        every { project.tasks } returns taskContainer
        every {
            taskContainer.register(
                "generateVersionCatalogAccessor",
                Task::class.java,
                any()
            )
        } answers {
            thirdArg<Action<Task>>().execute(generatorTask)
            generatorTaskProvider
        }
        every { taskContainer.named("compileKotlin", any()) } answers {
            secondArg<Action<Task>>().execute(compileKotlinTask)
            compileKotlinTaskProvider
        }

        // WHEN
        testSubject.addGeneratorTask(project)

        // THEN
        verifyAll {
            taskContainer.register("generateVersionCatalogAccessor", Task::class.java, any())
            generatorTask.dependsOn("generateVersionCatalogAccessorSource")

            taskContainer.named("compileKotlin", any())
            compileKotlinTask.dependsOn("generateVersionCatalogAccessor")
        }

        confirmVerified(
            taskContainer,
            generatorTaskProvider,
            generatorTask,
            compileKotlinTaskProvider,
            compileKotlinTask
        )
    }

    @Test
    fun `GIVEN project WHEN configureSourceSet() THEN sourceSet configured`() {
        // GIVEN
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("java-gradle-plugin")

        // WHEN
        testSubject.configureSourceSet(project)

        // THEN
        val javaExtension = project.extensions.getByName("java") as JavaPluginExtension
        val srcDirs = javaExtension.sourceSets.named("main").get().java.srcDirs
        assertEquals(2, srcDirs.size)
        assertEquals(
            "${project.buildDir}/generated/versionCatalogAccessor/src/main/kotlin",
            "${srcDirs.toList()[1]}"
        )
    }

    @Test
    fun `GIVEN project with jacoco WHEN configureCodeCoverage() THEN coverage configured`() {
        // GIVEN
        val pluginManager: PluginManager = mockk()
        val taskContainer: TaskContainer = mockk()
        val testTask: TestTask = mockk()
        val testTaskExtensionContainer: ExtensionContainer = mockk()
        val jacocoTaskExtension: JacocoTaskExtension = mockk(relaxed = true)
        val jacocoReportTaskProvider: TaskProvider<JacocoReport> = mockk()
        val jacocoReport: JacocoReport = mockk(relaxed = true)
        val jacocoCoverageVerificationTaskProvider: TaskProvider<JacocoCoverageVerification> = mockk()
        val jacocoCoverageVerification: JacocoCoverageVerification = mockk(relaxed = true)
        every { project.pluginManager } returns pluginManager
        every { pluginManager.hasPlugin("org.gradle.jacoco") } returns true
        every { project.tasks } returns taskContainer
        every { taskContainer.withType(TestTask::class.java).configureEach(any()) } answers {
            firstArg<Action<TestTask>>().execute(testTask)
        }
        every { testTask.extensions } returns testTaskExtensionContainer
        every { testTaskExtensionContainer.configure(JacocoTaskExtension::class.java, any()) } answers {
            secondArg<Action<JacocoTaskExtension>>().execute(jacocoTaskExtension)
        }
        every { taskContainer.withType(JacocoReport::class.java).configureEach(any()) } answers {
            firstArg<Action<JacocoReport>>().execute(jacocoReport)
        }
        every {
            taskContainer.withType(JacocoCoverageVerification::class.java).configureEach(any())
        } answers {
            firstArg<Action<JacocoCoverageVerification>>().execute(jacocoCoverageVerification)
        }
        val fileCollection: ConfigurableFileCollection = mockk(relaxed = true)
        every { project.files(any()) } returns fileCollection

        // WHEN
        testSubject.configureCodeCoverage(project)

        // THEN
        verifyAll {
            pluginManager.hasPlugin("org.gradle.jacoco")

            taskContainer.withType(TestTask::class.java).configureEach(any())
            testTask.extensions
            testTaskExtensionContainer.configure(JacocoTaskExtension::class.java, any())
            jacocoTaskExtension.excludes = any()

            taskContainer.withType(JacocoReport::class.java).configureEach(any())
            jacocoReport.classDirectories.files
            jacocoReport.classDirectories.setFrom(any())

            taskContainer.withType(JacocoCoverageVerification::class.java).configureEach(any())
            jacocoCoverageVerification.classDirectories.files
            jacocoCoverageVerification.classDirectories.setFrom(any())
        }

        confirmVerified(
            pluginManager,
            taskContainer,
            testTask,
            testTaskExtensionContainer,
            jacocoTaskExtension,
            jacocoReportTaskProvider,
            jacocoReport,
            jacocoCoverageVerificationTaskProvider,
            jacocoCoverageVerification
        )
    }

    @Test
    fun `GIVEN project without jacoco WHEN configureCodeCoverage() THEN coverage not configured`() {
        // GIVEN
        every { project.pluginManager.hasPlugin("org.gradle.jacoco") } returns false

        // WHEN
        testSubject.configureCodeCoverage(project)

        // THEN
        verify {
            project.pluginManager.hasPlugin("org.gradle.jacoco")
        }

        confirmVerified(project)
    }

    @Test
    fun `GIVEN project WHEN apply() THEN all configured`() {
        // GIVEN
        val pluginManager: PluginManager = mockk()
        val delegateProject = ProjectBuilder.builder().build()
        val project: Project = FakeProject(delegateProject, pluginManager)
        val spyTestSubject = spyk(testSubject)
        delegateProject.pluginManager.apply("java-gradle-plugin")
        delegateProject.pluginManager.apply("org.gradle.jacoco")
        project.tasks.maybeCreate("compileKotlin")
        every { pluginManager.hasPlugin("org.gradle.java-gradle-plugin") } returns true
        every { pluginManager.hasPlugin("org.gradle.kotlin.kotlin-dsl") } returns true
        every { pluginManager.hasPlugin("org.gradle.jacoco") } returns true

        // WHEN
        spyTestSubject.apply(project)

        // THEN
        verifyOrder {
            spyTestSubject.apply(project)
            spyTestSubject.checkPreconditions(project)
            spyTestSubject.addExtension(project)
            spyTestSubject.addSourceGeneratorTask(project, any<VersionCatalogAccessorPluginExtension>())
            spyTestSubject.addGeneratorTask(project)
            spyTestSubject.configureSourceSet(project)
            spyTestSubject.configureCodeCoverage(project)
        }

        confirmVerified(
            spyTestSubject
        )
    }
}
