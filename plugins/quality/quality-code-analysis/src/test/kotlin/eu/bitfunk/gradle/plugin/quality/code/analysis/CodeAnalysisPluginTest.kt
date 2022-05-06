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

package eu.bitfunk.gradle.plugin.quality.code.analysis

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyAll
import io.mockk.verifyOrder
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginManager
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CodeAnalysisPluginTest {

    private lateinit var project: Project

    private lateinit var testSubject: CodeAnalysisPlugin

    @BeforeEach
    fun setup() {
        project = mockk()

        testSubject = CodeAnalysisPlugin()
    }

    @Test
    fun plugin_implements_contract() {
        assertInstanceOf(
            CodeAnalysisContract.Plugin::class.java,
            testSubject
        )
    }

    @Test
    fun plugin_is_gradle_plugin() {
        assertInstanceOf(
            Plugin::class.java,
            testSubject
        )
    }

    @Test
    fun `GIVEN project WHEN appPlugins() THEN plugins added`() {
        // GIVEN
        val pluginManager: PluginManager = mockk(relaxed = true)
        every { project.pluginManager } returns pluginManager

        // WHEN
        testSubject.addPlugins(project)

        // THEN
        verify {
            pluginManager.apply("io.gitlab.arturbosch.detekt")
        }

        confirmVerified(pluginManager)
    }

    @Test
    fun `GIVEN project WHEN configureAnalysis() THEN analysis configured`() {
        // GIVEN
        val extensionContainer: ExtensionContainer = mockk()
        val detektExtension: DetektExtension = mockk()
        every { project.extensions } returns extensionContainer
        every { extensionContainer.configure(DetektExtension::class.java, any()) } answers {
            secondArg<Action<DetektExtension>>().execute(detektExtension)
        }

        // WHEN
        testSubject.configureAnalysis(project)

        // THEN
        verifyAll {
            extensionContainer.configure(DetektExtension::class.java, any())
        }

        confirmVerified(extensionContainer)
    }

    @Test
    fun `GIVEN project WHEN apply() THEN all configured`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)
        val spyTestSubject = spyk(testSubject)

        // WHEN
        testSubject.apply(project)

        // THEN
        verifyOrder {
            testSubject.addPlugins(project)
            testSubject.configureAnalysis(project)
        }

        confirmVerified(
            spyTestSubject
        )
    }
}
