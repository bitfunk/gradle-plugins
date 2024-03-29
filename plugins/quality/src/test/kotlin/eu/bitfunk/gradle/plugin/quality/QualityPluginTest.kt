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

package eu.bitfunk.gradle.plugin.quality

import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verifyAll
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class QualityPluginTest {

    private lateinit var testSubject: QualityPlugin

    @BeforeEach
    fun setup() {
        testSubject = QualityPlugin()
    }

    @Test
    fun `implements contract`() {
        assertInstanceOf(
            QualityContract.Plugin::class.java,
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

    @Test
    fun `GIVEN project WHEN addPlugins() THEN all plugins added`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)

        // WHEN
        testSubject.addPlugins(project)

        // THEN
        verifyAll {
            project.pluginManager.apply("eu.bitfunk.gradle.plugin.quality.code.analysis")
            project.pluginManager.apply("eu.bitfunk.gradle.plugin.quality.formatter")
            project.pluginManager.apply("eu.bitfunk.gradle.plugin.quality.report")
        }

        confirmVerified(project)
    }

    @Test
    fun `GIVEN project WHEN apply() THEN all configured`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)
        val spyTestSubject = spyk(testSubject)

        // WHEN
        spyTestSubject.apply(project)

        // THEN
        verifyAll {
            spyTestSubject.apply(project)
            spyTestSubject.addPlugins(project)
        }

        confirmVerified(spyTestSubject)
    }
}
