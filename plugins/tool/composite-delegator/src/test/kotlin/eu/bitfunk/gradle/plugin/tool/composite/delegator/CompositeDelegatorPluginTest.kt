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
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.initialization.IncludedBuild
import org.gradle.api.tasks.Delete
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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

    @Suppress("UnusedEquals")
    @Test
    fun `GIVEN project WHEN addExtension() THEN extension added`() {
        // GIVEN
        val project: Project = mockk()
        val extension: CompositeDelegatorPluginExtension = mockk(relaxed = true)
        every {
            project.extensions.create(
                any(),
                CompositeDelegatorPluginExtension::class.java
            )
        } returns extension

        // WHEN
        val result = testSubject.addExtension(project)

        // THEN
        assertEquals(
            extension,
            result
        )

        verifyAll {
            project.extensions.create("compositeDelegator", CompositeDelegatorPluginExtension::class.java)

            extension.additionalTasks.convention(emptyList())

            extension.equals(result)
        }

        confirmVerified(project, extension)
    }

    @Test
    fun `GIVEN project and extension WHEN configureTasks THEN tasks configured`() {
        // GIVEN
        val project: Project = mockk()
        val extension: Extension = mockk()
        val task: Task = mockk(relaxed = true)
        val includedBuild: IncludedBuild = mockk(relaxed = true)
        val includedBuildsList: List<IncludedBuild> = mutableListOf(includedBuild)
        every { project.tasks.maybeCreate(any()) } returns task
        every { project.tasks.maybeCreate(any(), any<Class<Task>>()) } returns task
        every { project.gradle.includedBuilds } returns includedBuildsList

        // WHEN
        testSubject.configureTasks(project, extension)

        // THEN
        verifyAll {
            for (pair in TASKS) {
                if (pair.second != null) {
                    project.tasks.maybeCreate(pair.first, pair.second!!)
                } else {
                    project.tasks.maybeCreate(pair.first)
                }
                project.gradle.includedBuilds
                task.dependsOn(any())
                includedBuild.task(":${pair.first}")
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

        // WHEN
        spyTestSubject.apply(project)

        // THEN
        verifyOrder {
            spyTestSubject.apply(project)
            spyTestSubject.addExtension(project)
            spyTestSubject.configureTasks(project, extension)
        }

        confirmVerified(
            spyTestSubject
        )
    }

    private companion object {
        private val TASKS = listOf(
            Pair("assemble", null),
            Pair("build", null),
            Pair("check", null),
            Pair("clean", Delete::class.java),
            Pair("jacocoTestReport", null),
            Pair("test", org.gradle.api.tasks.testing.Test::class.java),
            Pair("wrapper", null),
            Pair("dependencyUpdates", null),
            Pair("versionCatalogUpdate", null),
        )
    }
}
