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

package eu.bitfunk.gradle.plugin.tool.gitversion

import eu.bitfunk.gradle.plugin.development.test.util.stubGradleAction
import eu.bitfunk.gradle.plugin.development.test.util.stubGradleActionWithReturn
import groovy.lang.Closure
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyAll
import io.mockk.verifyOrder
import org.eclipse.jgit.api.Git
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.invoke
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class GitVersionPluginTest {

    @TempDir
    private lateinit var tempDir: File

    private lateinit var project: Project

    private lateinit var testSubject: GitVersionPlugin

    @BeforeEach
    fun setup() {
        project = mockk()

        testSubject = GitVersionPlugin()
    }

    @Test
    fun `implements contract`() {
        assertInstanceOf(
            GitVersionContract.Plugin::class.java,
            testSubject
        )
    }

    @Test
    fun `GIVEN project with plugin WHEN addExtension() THEN extension is added to project with defaults`() {
        // GIVEN
        every { project.rootProject } returns project
        val extensions: ExtensionContainer = mockk()
        every { project.extensions } returns extensions
        val extension: GitVersionPluginExtension = mockk(relaxed = true)
        every {
            extensions.create(any(), GitVersionPluginExtension::class.java)
        } returns extension

        // WHEN
        val result = testSubject.addExtension(project)

        // THEN
        Assertions.assertEquals(
            extension,
            result
        )

        verify { extensions.create("gitVersionConfig", GitVersionPluginExtension::class.java) }
        verify { extension.prefix.convention("") }
    }

    @Test
    fun `GIVEN project WHEN addExtraProperties() THEN properties available`() {
        // GIVEN
        val extensionContainer: ExtensionContainer = mockk()
        val extraPropertiesExtension: ExtraPropertiesExtension = mockk(relaxed = true)
        val extension: GitVersionPluginExtension = mockk()
        val taskContainer: TaskContainer = mockk()
        every { project.extensions } returns extensionContainer
        every { extension.prefix.get() } returns ""
        every { project.tasks } returns taskContainer
        every { extensionContainer.extraProperties } returns extraPropertiesExtension
        val gitVersionSlot = slot<Closure<String>>()
        val gitVersionInfoSlot = slot<Closure<GitVersionInfo>>()
        every {
            extraPropertiesExtension.set(
                "gitVersion",
                capture(gitVersionSlot)
            )
        } returns Unit
        every { extraPropertiesExtension.set("gitVersionInfo", capture(gitVersionInfoSlot)) } returns Unit

        val git: Git = Git.init().setDirectory(tempDir).call()

        // WHEN
        testSubject.addExtraProperties(project, extension, git)
        gitVersionSlot.captured.invoke()
        gitVersionInfoSlot.captured.invoke()

        // THEN
        verifyAll {
            extraPropertiesExtension.set("gitVersion", any())
            extraPropertiesExtension.set("gitVersionInfo", any())

            extension.prefix.get()
        }

        confirmVerified(
            extension,
            taskContainer,
            extraPropertiesExtension,
        )
    }

    @Test
    fun `GIVEN project WHEN configureVersionTasks() THEN version tasks configured`() {
        // GIVEN
        val taskContainer: TaskContainer = mockk()
        val taskPrintGitVersion: Task = mockk(relaxed = true)
        val taskPrintGitVersionInfo: Task = mockk(relaxed = true)
        every { project.tasks } returns taskContainer
        every { taskContainer.create("printGitVersion", any<Action<Task>>()) } answers {
            secondArg<Action<Task>>().execute(taskPrintGitVersion)
            taskPrintGitVersion
        }
        every { taskContainer.create("printGitVersionInfo", any<Action<Task>>()) } answers {
            secondArg<Action<Task>>().execute(taskPrintGitVersionInfo)
            taskPrintGitVersionInfo
        }
        every { taskPrintGitVersion.doLast(any<Action<Task>>()) } answers {
            firstArg<Action<Task>>().execute(taskPrintGitVersion)
            taskPrintGitVersion
        }
        every { taskPrintGitVersionInfo.doLast(any<Action<Task>>()) } answers {
            firstArg<Action<Task>>().execute(taskPrintGitVersionInfo)
            taskPrintGitVersionInfo
        }
        val extensionContainer: ExtensionContainer = mockk()
        val gitVersionClosure: Closure<String> = mockk()
        val gitVersionInfoClosure: Closure<GitVersionInfo> = mockk()
        every { project.extensions } returns extensionContainer
        every { extensionContainer.extraProperties.get("gitVersion") } returns gitVersionClosure
        every { gitVersionClosure.call() } returns "gitVersion"
        every { extensionContainer.extraProperties.get("gitVersionInfo") } returns gitVersionInfoClosure
        every { gitVersionInfoClosure.call() } returns mockk(relaxed = true)

        // WHEN
        testSubject.configureVersionTasks(project)

        // THEN
        verifyAll {
            taskContainer.create("printGitVersion", any<Action<Task>>())
            taskContainer.create("printGitVersionInfo", any<Action<Task>>())

            taskPrintGitVersion.group = "versioning"
            taskPrintGitVersion.description = "Prints the git version to standard out"
            taskPrintGitVersion.doLast(any<Action<Task>>())

            taskPrintGitVersionInfo.group = "versioning"
            taskPrintGitVersionInfo.description = "Prints the project's git version info to standard out"
            taskPrintGitVersionInfo.doLast(any<Action<Task>>())
        }

        confirmVerified(
            taskContainer,
            taskPrintGitVersion,
            taskPrintGitVersionInfo,
        )
    }

    @Test
    fun `GIVEN project WHEN apply() THEN all configured`() {
        // GIVEN
        val project: Project = mockk()
        val spyTestSubject = spyk(testSubject)
        val extensionContainer: ExtensionContainer = mockk(relaxed = true)
        val extension: GitVersionPluginExtension = mockk(relaxed = true)
        val taskContainer: TaskContainer = mockk()
        every { project.extensions } returns extensionContainer
        every { extensionContainer.create(any(), GitVersionPluginExtension::class.java) } returns extension
        every { project.projectDir } returns tempDir
        every { project.tasks } returns taskContainer
        every { taskContainer.create("printGitVersion", any<Action<Task>>()) } returns mockk()
        every { taskContainer.create("printGitVersionInfo", any<Action<Task>>()) } returns mockk()

        Git.init().setDirectory(tempDir).call()

        // WHEN
        spyTestSubject.apply(project)

        // THEN
        verifyOrder {
            spyTestSubject.apply(project)
            spyTestSubject.addExtension(project)
            spyTestSubject.addExtraProperties(project, extension, any())
            spyTestSubject.configureVersionTasks(project)
        }

        confirmVerified(
            spyTestSubject
        )
    }
}
