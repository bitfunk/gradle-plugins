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

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyAll
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.kotlin.dsl.extra
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProjectExtensionKtTest {

    @Test
    fun `GIVEN project WHEN version() THEN version returned`() {
        // GIVEN
        val project: Project = mockk()
        val version = "1.0.0"
        every { project.version } returns version

        // WHEN
        val result = project.version()

        // THEN
        assertEquals(
            version,
            result
        )

        verify {
            project.version
        }

        confirmVerified(project)
    }

    @Test
    fun `GIVEN project, snapshot version WHEN versionCleaned() THEN version returned`() {
        // GIVEN
        val project: Project = mockk()
        val version = "1.0.0-SNAPSHOT"
        every { project.version } returns version

        // WHEN
        val result = project.versionCleaned()

        // THEN
        assertEquals(
            "1.0.0",
            result
        )

        verify { project.version }

        confirmVerified(project)
    }

    @Test
    fun `GIVEN project, feature snapshot version WHEN versionCleaned() THEN version returned`() {
        // GIVEN
        val project: Project = mockk()
        val version = "1.0.0-add-some-feature-SNAPSHOT"
        every { project.version } returns version

        // WHEN
        val result = project.versionCleaned()

        // THEN
        assertEquals(
            "1.0.0",
            result
        )

        verify { project.version }

        confirmVerified(project)
    }

    @Test
    fun `GIVEN project, final version WHEN versionCleaned() THEN version returned`() {
        // GIVEN
        val project: Project = mockk()
        val version = "1.0.0"
        every { project.version } returns version

        // WHEN
        val result = project.versionCleaned()

        // THEN
        assertEquals(
            "1.0.0",
            result
        )

        verify { project.version }

        confirmVerified(project)
    }

    @Test
    fun `GIVEN project WHEN versionCode() THEN versionCode returned`() {
        // GIVEN
        val project: Project = mockk()
        val extraPropertiesExtension: ExtraPropertiesExtension = mockk()
        every { project.extra } returns extraPropertiesExtension
        every { extraPropertiesExtension.get("versionCode") } returns 1234

        // WHEN
        val result = project.versionCode()

        // THEN
        assertEquals(
            1234,
            result
        )

        verifyAll {
            project.extra
            extraPropertiesExtension.get("versionCode")
        }

        confirmVerified(project)
    }

    @Test
    fun `GIVEN project WHEN versionCodeFeature() THEN versionCode returned`() {
        // GIVEN
        val project: Project = mockk()
        val extraPropertiesExtension: ExtraPropertiesExtension = mockk()
        every { project.extra } returns extraPropertiesExtension
        every { extraPropertiesExtension.get("versionCodeFeature") } returns 1234

        // WHEN
        val result = project.versionCodeFeature()

        // THEN
        assertEquals(
            1234,
            result
        )

        verifyAll {
            project.extra
            extraPropertiesExtension.get("versionCodeFeature")
        }

        confirmVerified(project)
    }
}
