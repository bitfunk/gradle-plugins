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

package eu.bitfunk.gradle.plugin.tool.composite.delegator

import eu.bitfunk.gradle.plugin.tool.composite.delegator.CompositeDelegatorContract.Extension
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verifyAll
import io.mockk.verifyOrder
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.initialization.IncludedBuild
import org.gradle.api.tasks.Delete
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CompositeDelegatorPluginTest {

    private lateinit var testSubject: CompositeDelegatorPlugin

    @BeforeEach
    fun setup() {
        testSubject = CompositeDelegatorPlugin()
    }

    @Test
    fun `implements contract`() {
        assertInstanceOf(
            CompositeDelegatorContract.Plugin::class.java,
            testSubject,
        )
    }

    @Test
    fun `implements plugin`() {
        assertInstanceOf(
            Plugin::class.java,
            testSubject,
        )
    }

    @Suppress("UnusedEquals")
    @Test
    fun `GIVEN project WHEN addExtension() THEN extension added`() {
        // GIVEN
        val project: Project = mockk()
        val extension: CompositeDelegatorPluginExtension = mockk(relaxed = true)
        every {
            project.extensions.create(
                any(),
                CompositeDelegatorPluginExtension::class.java,
            )
        } returns extension

        // WHEN
        val result = testSubject.addExtension(project)

        // THEN
        assertEquals(
            extension,
            result,
        )

        verifyAll {
            project.extensions.create("compositeDelegator", CompositeDelegatorPluginExtension::class.java)

            extension.additionalTasks.convention(emptyList())

            extension.equals(result)
        }

        confirmVerified(project, extension)
    }

    @Test
    fun `GIVEN project and extension WHEN configureTasks() THEN tasks configured`() {
        // GIVEN
        val project: Project = mockk()
        val extension: Extension = mockk()
        val task: Task = mockk(relaxed = true)
        val includedBuild: IncludedBuild = mockk(relaxed = true)
        val includedBuildsList: List<IncludedBuild> = mutableListOf(includedBuild)
        every { project.tasks.maybeCreate(any()) } returns task
        every { project.tasks.maybeCreate(any(), any<Class<Task>>()) } returns task
        every { project.gradle.includedBuilds } returns includedBuildsList
        every { extension.additionalTasks.get() } returns listOf("task1", "task2")
        every { project.afterEvaluate(any<Action<Project>>()) } answers {
            firstArg<Action<Project>>().execute(project)
        }

        // WHEN
        testSubject.configureTasks(project, extension)

        // THEN
        verifyAll {
            project.afterEvaluate(any<Action<Project>>())
            extension.additionalTasks

            for (item in TASKS) {
                if (item.value != null) {
                    project.tasks.maybeCreate(item.key, item.value!!)
                } else {
                    project.tasks.maybeCreate(item.key)
                }
                project.gradle.includedBuilds
                task.dependsOn(any())
                includedBuild.task(":${item.key}")
            }
        }

        confirmVerified(project, extension, task, includedBuild)
    }

    @Test
    fun `GIVEN project WHEN apply() THEN everything configured`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)
        val spyTestSubject = spyk(testSubject)
        val extension: CompositeDelegatorPluginExtension = mockk(relaxed = true)
        val task: Task = mockk(relaxed = true)
        every { project.extensions.create(any(), CompositeDelegatorPluginExtension::class.java) } returns extension
        every { project.tasks.maybeCreate(any()) } returns task
        every { project.tasks.maybeCreate(any(), any<Class<Task>>()) } returns task
        every { extension.additionalTasks.get() } returns emptyList()

        // WHEN
        spyTestSubject.apply(project)

        // THEN
        verifyOrder {
            spyTestSubject.apply(project)
            spyTestSubject.addExtension(project)
            spyTestSubject.configureTasks(project, extension)
        }

        confirmVerified(
            spyTestSubject,
        )
    }

    private companion object {
        private val TASKS = mapOf(
            "assemble" to null,
            "build" to null,
            "check" to null,
            "clean" to Delete::class.java,
            "jacocoTestReport" to null,
            "test" to org.gradle.api.tasks.testing.Test::class.java,
            "wrapper" to null,
            "dependencyUpdates" to null,
            "versionCatalogUpdate" to null,

            // Custom
            "task1" to null,
            "task2" to null,
        )
    }
}
