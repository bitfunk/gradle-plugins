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

import groovy.lang.Closure
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ProjectExtensionKtTest {

    @Test
    fun `GIVEN project WHEN gitVersion() THEN gitVersion returned`() {
        // GIVEN
        val project: Project = mockk()
        val closure: Closure<String> = mockk()
        val gitVersion = "1.0.0"
        every { project.extensions.extraProperties.get("gitVersion") } returns closure
        every { closure.invoke() } returns gitVersion

        // WHEN
        val result = project.gitVersion()

        // THEN
        assertEquals(
            gitVersion,
            result,
        )
    }

    @Test
    fun `GIVEN project WHEN gitVersionInfo() THEN gitVersionInfo returned`() {
        // GIVEN
        val project: Project = mockk()
        val closure: Closure<GitVersionInfo> = mockk()
        val gitVersionInfo: GitVersionInfo = mockk()
        every { project.extensions.extraProperties.get("gitVersionInfo") } returns closure
        every { closure.invoke() } returns gitVersionInfo

        // WHEN
        val result = project.gitVersionInfo()

        // THEN
        assertEquals(
            gitVersionInfo,
            result,
        )
    }
}
