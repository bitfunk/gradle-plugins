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

package eu.bitfunk.gradle.plugin.development.test.util

import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verifyAll
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.junit.jupiter.api.Test
import java.io.File
import org.gradle.api.tasks.testing.Test as TestTask

internal class GradleActionMockKtTest {

    @Test
    fun `GIVEN project WHEN stubGradleAction THEN action executed`() {
        // GIVEN
        val project: Project = mockk()
        val evaluatedProject: Project = mockk(relaxed = true)
        val dir: File = mockk()

        // WHEN
        stubGradleAction(
            evaluatedProject
        ) { project.afterEvaluate(it) }

        project.afterEvaluate {
            buildDir = dir
        }

        // THEN
        verifyAll {
            project.afterEvaluate(any<Action<Project>>())

            evaluatedProject.buildDir = dir
        }

        confirmVerified(project)
    }

    @Test
    fun `GIVEN project WHEN stubGradleActionWithReturn THEN action executed and returns`() {
        // GIVEN
        val project: Project = mockk()
        val testTaskProvider: TaskProvider<TestTask> = mockk()
        val testTask: TestTask = mockk(relaxed = true)

        // WHEN
        stubGradleActionWithReturn(
            testTask,
            testTaskProvider
        ) { project.tasks.named("test", TestTask::class.java, it) }

        project.tasks.named("test", TestTask::class.java) {
            exclude("exclude")
        }

        // THEN
        verifyAll {
            project.tasks.named("test", TestTask::class.java, any())

            testTask.exclude("exclude")
        }

        confirmVerified(project)
    }
}
