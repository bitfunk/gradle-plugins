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

package eu.bitfunk.gradle.plugin.tool.versioning

import eu.bitfunk.gradle.plugin.tool.versioning.VersioningContract.Generator
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyAll
import io.mockk.verifyOrder
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.extra
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VersioningPluginTest {

    private lateinit var project: Project

    private lateinit var testSubject: VersioningPlugin

    @BeforeEach
    fun setup() {
        project = mockk()

        testSubject = VersioningPlugin()
    }

    @Test
    fun plugin_implements_contract() {
        assertInstanceOf(
            VersioningContract.Plugin::class.java,
            testSubject,
        )
    }

    @Test
    fun `GIVEN project WHEN addPlugins() THEN plugins added`() {
        // GIVEN
        val pluginManager: PluginManager = mockk(relaxed = true)
        every { project.pluginManager } returns pluginManager

        // WHEN
        testSubject.addPlugins(project)

        // THEN
        verify {
            pluginManager.apply("eu.bitfunk.gradle.plugin.tool.gitversion")
        }

        confirmVerified(pluginManager)
    }

    @Test
    fun `GIVEN project WHEN configureVersion() THEN version configured`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)
        val versionName = "versionName"
        val versionCode = 123
        val featureVersionCode = 456
        val generator: Generator = mockk(relaxed = true)
        val allProject: Project = mockk(relaxed = true)
        every { project.allprojects(any<Action<Project>>()) } answers {
            firstArg<Action<Project>>().execute(allProject)
        }
        every { generator.generateVersionName() } returns versionName
        every { generator.generateVersionCode() } returns versionCode
        every { generator.generateFeatureVersionCode(any()) } returns featureVersionCode

        // WHEN
        testSubject.configureVersion(project, generator)

        // THEN
        verifyAll {
            project.allprojects(any<Action<Project>>())

            generator.generateVersionName()
            generator.generateVersionCode()
            generator.generateFeatureVersionCode(any())

            allProject.version = versionName
            allProject.extra.set("versionCode", versionCode)
            allProject.extra.set("versionCodeFeature", featureVersionCode)
        }

        confirmVerified(
            project,
            generator,
            allProject,
        )
    }

    @Test
    fun `GIVEN project WHEN configureVersionTasks() THEN version tasks configured`() {
        // GIVEN
        val generator: Generator = mockk(relaxed = true)
        val taskContainer: TaskContainer = mockk()
        val taskProvider: TaskProvider<Task> = mockk()
        val task: Task = mockk(relaxed = true)
        every { project.tasks } returns taskContainer
        every { taskContainer.register("versionInfo", any()) } answers {
            secondArg<Action<Task>>().execute(task)
            taskProvider
        }
        every { task.doLast(any<Action<Task>>()) } answers {
            firstArg<Action<Task>>().execute(task)
            task
        }

        // WHEN
        testSubject.configureVersionTasks(project, generator)

        // THEN
        verifyAll {
            taskContainer.register("versionInfo", any())

            task.group = "versioning"
            task.doLast(any<Action<Task>>())

            generator.generateVersionName()
            generator.generateVersionCode()
            generator.generateFeatureVersionCode(any())
            generator.generateVersionDetails()
        }

        confirmVerified(
            generator,
            taskContainer,
            task,
        )
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
            spyTestSubject.addPlugins(project)
            spyTestSubject.configureVersion(project, any())
            spyTestSubject.configureVersionTasks(project, any())
        }

        confirmVerified(
            spyTestSubject,
        )
    }
}
