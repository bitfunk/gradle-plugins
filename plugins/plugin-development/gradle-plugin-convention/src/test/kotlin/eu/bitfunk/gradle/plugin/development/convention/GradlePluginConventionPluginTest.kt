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

package eu.bitfunk.gradle.plugin.development.convention

import eu.bitfunk.gradle.plugin.common.test.util.stubGradleAction
import eu.bitfunk.gradle.plugin.common.test.util.stubGradleActionWithReturn
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verifyAll
import io.mockk.verifyOrder
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion.VERSION_11
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoReportsContainer
import org.gradle.testing.jacoco.tasks.rules.JacocoLimit
import org.gradle.testing.jacoco.tasks.rules.JacocoViolationRule
import org.gradle.testing.jacoco.tasks.rules.JacocoViolationRulesContainer
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.math.BigDecimal
import org.gradle.api.tasks.testing.Test as TestTask

class GradlePluginConventionPluginTest {

    private lateinit var project: Project

    private lateinit var testSubject: GradlePluginConventionPlugin

    @BeforeEach
    fun setup() {
        project = mockk()

        testSubject = GradlePluginConventionPlugin()
    }

    @Test
    fun `plugin implements contract`() {
        assertInstanceOf(
            GradlePluginConventionContract.Plugin::class.java,
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
    fun `GIVEN project WHEN checkPreconditions() THEN conditions are met`() {
        // GIVEN
        mockkStatic(GradleVersion::class)
        every { GradleVersion.current() } returns GradleVersion.version("7.2")

        // WHEN/THEN
        assertDoesNotThrow {
            testSubject.checkPreconditions(project)
        }

        unmockkAll() // mockStatic!!
    }

    @Test
    fun `GIVEN project WHEN addPlugins() THEN all plugins present`() {
        // GIVEN
        val pluginManager: PluginManager = mockk(relaxed = true)
        every { project.pluginManager } returns pluginManager

        // WHEN
        testSubject.addPlugins(project)

        // THEN
        verifyAll {
            pluginManager.apply("org.gradle.java-gradle-plugin")
            pluginManager.apply("org.gradle.kotlin.kotlin-dsl")
            pluginManager.apply("org.gradle.jacoco")
            pluginManager.apply("org.jetbrains.kotlinx.binary-compatibility-validator")
        }

        confirmVerified(pluginManager)
    }

    @Test
    fun `GIVEN project WHEN addRepositories() THEN mavenCentral available`() {
        // GIVEN
        val repositoryHandler: RepositoryHandler = mockk(relaxed = true)
        every { project.repositories } returns repositoryHandler

        // WHEN
        testSubject.addRepositories(project)

        // THEN
        verifyOrder {
            repositoryHandler.gradlePluginPortal()
            repositoryHandler.mavenCentral()
            repositoryHandler.google()
        }

        confirmVerified(repositoryHandler)
    }

    @Test
    fun `GIVEN project WHEN configureJavaCompatibility() THEN java configured`() {
        // GIVEN
        val extensionContainer: ExtensionContainer = mockk()
        val javaPluginExtension: JavaPluginExtension = mockk(relaxed = true)
        every { project.extensions } returns extensionContainer
        stubGradleAction(javaPluginExtension) { extensionContainer.configure(JavaPluginExtension::class.java, it) }

        // WHEN
        testSubject.configureJavaCompatibility(project)

        // THEN
        verifyAll {
            extensionContainer.configure(JavaPluginExtension::class.java, any())

            javaPluginExtension.sourceCompatibility = VERSION_11
            javaPluginExtension.targetCompatibility = VERSION_11
        }

        confirmVerified(extensionContainer, javaPluginExtension)
    }

    @Test
    fun `GIVEN project WHEN configureKotlin() THEN kotlin configured`() {
        // GIVEN
        val extensionContainer: ExtensionContainer = mockk()
        val kotlinJvmProjectExtension: KotlinJvmProjectExtension = mockk(relaxed = true)
        every { project.extensions } returns extensionContainer
        every { extensionContainer.configure(KotlinJvmProjectExtension::class.java, any()) } answers {
            secondArg<Action<KotlinJvmProjectExtension>>().execute(kotlinJvmProjectExtension)
        }

        // WHEN
        testSubject.configureKotlin(project)

        // THEN
        verifyAll {
            extensionContainer.configure(KotlinJvmProjectExtension::class.java, any())

            kotlinJvmProjectExtension.explicitApi()
        }

        confirmVerified(extensionContainer, kotlinJvmProjectExtension)
    }

    @Test
    fun `GIVEN project WHEN configureDependencies() THEN dependencies added`() {
        // GIVEN
        val dependencyHandlerScope: DependencyHandlerScope = mockk(relaxed = true)
        val gradleTestKitDependency: Dependency = mockk()
        every { project.dependencies } returns dependencyHandlerScope
        every { dependencyHandlerScope.gradleTestKit() } returns gradleTestKitDependency

        // WHEN
        testSubject.configureDependencies(project)

        // THEN
        verifyAll {
            dependencyHandlerScope.gradleTestKit()

            dependencyHandlerScope.add("testImplementation", gradleTestKitDependency)
            dependencyHandlerScope.add("testImplementation", "org.junit.jupiter:junit-jupiter:5.8.2")
            dependencyHandlerScope.add("testRuntimeOnly", "org.junit.jupiter:junit-jupiter-engine:5.8.2")
            dependencyHandlerScope.add("testImplementation", "io.mockk:mockk:1.12.2")
            dependencyHandlerScope.add(
                "testImplementation",
                "eu.bitfunk.gradle.plugin.common.test:gradle-test-util:0.1.0"
            )
        }

        confirmVerified(dependencyHandlerScope)
    }

    @Test
    fun `GIVEN project WHEN configureTests() THEN tests configured`() {
        // GIVEN
        val taskContainer: TaskContainer = mockk()
        val taskCollection: TaskCollection<TestTask> = mockk()
        val testTask: TestTask = mockk(relaxed = true)
        every { project.tasks } returns taskContainer
        every { taskContainer.withType(TestTask::class.java) } returns taskCollection
        every { taskCollection.configureEach(any()) } answers {
            firstArg<Action<TestTask>>().execute(testTask)
        }

        // WHEN
        testSubject.configureTests(project)

        // THEN
        verifyAll {
            taskContainer.withType(TestTask::class.java)
            taskCollection.configureEach(any())

            testTask.useJUnitPlatform()
        }

        confirmVerified(taskContainer, taskCollection, testTask)
    }

    @Test
    fun `GIVEN project WHEN configureTestCoverage() THEN test coverage configured`() {
        // GIVEN
        val extensionContainer: ExtensionContainer = mockk()
        val jacocoPluginExtension: JacocoPluginExtension = mockk(relaxed = true)
        every { project.extensions } returns extensionContainer
        every { extensionContainer.configure(JacocoPluginExtension::class.java, any()) } answers {
            secondArg<Action<JacocoPluginExtension>>().execute(jacocoPluginExtension)
        }

        // WHEN
        testSubject.configureTestCoverage(project)

        // THEN
        verifyAll {
            extensionContainer.configure(JacocoPluginExtension::class.java, any())

            jacocoPluginExtension.toolVersion = "0.8.8"
        }

        confirmVerified(extensionContainer, jacocoPluginExtension)
    }

    @Test
    fun `GIVEN project WHEN configureTestCoverageTasks() THEN tasks configured`() {
        // GIVEN
        val taskContainer: TaskContainer = mockk()
        every { project.tasks } returns taskContainer
        val taskDependencyProvider: TaskProvider<Task> = mockk()

        val jacocoReport: JacocoReport = mockk(relaxed = true)
        val jacocoReportsContainer: JacocoReportsContainer = mockk(relaxed = true)
        val htmlBooleanProperty: Property<Boolean> = mockk(relaxed = true)
        val xmlBooleanProperty: Property<Boolean> = mockk(relaxed = true)
        configureGivenJacocoTestReportTask(
            taskContainer,
            jacocoReport,
            jacocoReportsContainer,
            taskDependencyProvider,
            htmlBooleanProperty,
            xmlBooleanProperty
        )

        val jacocoCoverageVerification: JacocoCoverageVerification = mockk(relaxed = true)
        val jacocoViolationRulesContainer: JacocoViolationRulesContainer = mockk()
        val jacocoViolationRule: JacocoViolationRule = mockk()
        val jacocoLimit: JacocoLimit = mockk(relaxed = true)
        configureGivenJacocoTestCoverageVerificationTask(
            taskContainer,
            jacocoCoverageVerification,
            jacocoViolationRulesContainer,
            jacocoViolationRule,
            jacocoLimit,
            taskDependencyProvider
        )

        val checkTask: Task = mockk(relaxed = true)
        configureGivenCheckTask(taskContainer, checkTask, taskDependencyProvider)

        // WHEN
        testSubject.configureTestCoverageTasks(project)

        // THEN
        verifyAll {
            taskContainer.named("jacocoTestReport", JacocoReport::class.java, any())
            taskContainer.named("test")
            jacocoReport.dependsOn(taskDependencyProvider)
            jacocoReport.reports(any<Action<JacocoReportsContainer>>())
            jacocoReportsContainer.html
            htmlBooleanProperty.set(true)
            jacocoReportsContainer.xml
            xmlBooleanProperty.set(true)

            taskContainer.named("jacocoTestCoverageVerification", JacocoCoverageVerification::class.java, any())
            taskContainer.named("jacocoTestReport")
            jacocoCoverageVerification.dependsOn(taskDependencyProvider)
            jacocoCoverageVerification.violationRules(any())
            jacocoViolationRulesContainer.rule(any())
            jacocoViolationRule.limit(any())
            jacocoLimit.minimum = BigDecimal(0.95)

            taskContainer.named("check", any())
            taskContainer.named("jacocoTestCoverageVerification")
            checkTask.dependsOn(taskDependencyProvider)
        }

        confirmVerified(
            taskContainer,
            taskDependencyProvider,
            jacocoReport, jacocoReportsContainer, htmlBooleanProperty, xmlBooleanProperty,
            jacocoCoverageVerification, jacocoViolationRulesContainer, jacocoViolationRule, jacocoLimit,
            checkTask
        )
    }

    private fun configureGivenJacocoTestReportTask(
        taskContainer: TaskContainer,
        jacocoReport: JacocoReport,
        jacocoReportsContainer: JacocoReportsContainer,
        taskDependencyProvider: TaskProvider<Task>,
        htmlBooleanProperty: Property<Boolean>,
        xmlBooleanProperty: Property<Boolean>
    ) {
        stubGradleActionWithReturn(jacocoReport, mockk()) {
            taskContainer.named("jacocoTestReport", JacocoReport::class.java, it)
        }
        every { taskContainer.named("test") } returns taskDependencyProvider
        every { jacocoReport.reports(any<Action<JacocoReportsContainer>>()) } answers {
            firstArg<Action<JacocoReportsContainer>>().execute(jacocoReportsContainer)
            jacocoReportsContainer
        }
        every { jacocoReportsContainer.html.required } returns htmlBooleanProperty
        every { jacocoReportsContainer.xml.required } returns xmlBooleanProperty
    }

    private fun configureGivenJacocoTestCoverageVerificationTask(
        taskContainer: TaskContainer,
        jacocoCoverageVerification: JacocoCoverageVerification,
        jacocoViolationRulesContainer: JacocoViolationRulesContainer,
        jacocoViolationRule: JacocoViolationRule,
        jacocoLimit: JacocoLimit,
        taskDependencyProvider: TaskProvider<Task>
    ) {
        every {
            taskContainer.named(
                "jacocoTestCoverageVerification",
                JacocoCoverageVerification::class.java,
                any()
            )
        } answers {
            thirdArg<Action<JacocoCoverageVerification>>().execute(jacocoCoverageVerification)
            mockk()
        }
        every { jacocoCoverageVerification.violationRules(any()) } answers {
            firstArg<Action<JacocoViolationRulesContainer>>().execute(jacocoViolationRulesContainer)
            jacocoViolationRulesContainer
        }
        every { jacocoViolationRulesContainer.rule(any()) } answers {
            firstArg<Action<JacocoViolationRule>>().execute(jacocoViolationRule)
            jacocoViolationRule
        }
        every { jacocoViolationRule.limit(any()) } answers {
            firstArg<Action<JacocoLimit>>().execute(jacocoLimit)
            jacocoLimit
        }
        every { taskContainer.named("jacocoTestReport") } returns taskDependencyProvider
    }

    private fun configureGivenCheckTask(
        taskContainer: TaskContainer,
        checkTask: Task,
        taskDependencyProvider: TaskProvider<Task>
    ) {
        every { taskContainer.named("check", any()) } answers {
            secondArg<Action<Task>>().execute(checkTask)
            mockk()
        }
        every { taskContainer.named("jacocoTestCoverageVerification") } returns taskDependencyProvider
    }

    @Test
    fun `GIVEN project WHEN configureGradleWrapper() THEN wrapper configured`() {
        // GIVEN
        val taskContainer: TaskContainer = mockk()
        val taskProvider: TaskProvider<Wrapper> = mockk()
        val wrapperTask: Wrapper = mockk(relaxed = true)
        every { project.tasks } returns taskContainer
        every { taskContainer.named("wrapper", Wrapper::class.java, any()) } answers {
            thirdArg<Action<Wrapper>>().execute(wrapperTask)
            taskProvider
        }

        // WHEN
        testSubject.configureGradleWrapper(project)

        // THEN
        verifyAll {
            taskContainer.named("wrapper", Wrapper::class.java, any())

            wrapperTask.gradleVersion = "7.5.1"
            wrapperTask.distributionType = Wrapper.DistributionType.ALL
        }

        confirmVerified(taskContainer, taskProvider, wrapperTask)
    }

    @Test
    fun `GIVEN project WHEN apply() THEN all configured`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)
        val spyTestSubject = spyk(testSubject)

        // WHEN
        spyTestSubject.apply(project)

        // THEN
        verifyOrder {
            spyTestSubject.apply(project)
            spyTestSubject.checkPreconditions(project)
            spyTestSubject.addPlugins(project)
            spyTestSubject.addRepositories(project)
            spyTestSubject.configureJavaCompatibility(project)
            spyTestSubject.configureKotlin(project)
            spyTestSubject.configureDependencies(project)
            spyTestSubject.configureTests(project)
            spyTestSubject.configureTestCoverage(project)
            spyTestSubject.configureTestCoverageTasks(project)
            spyTestSubject.configureGradleWrapper(project)
        }

        confirmVerified(
            spyTestSubject
        )
    }
}
