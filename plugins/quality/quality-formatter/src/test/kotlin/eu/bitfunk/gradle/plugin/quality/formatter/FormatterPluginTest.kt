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

package eu.bitfunk.gradle.plugin.quality.formatter

import com.diffplug.gradle.spotless.FormatExtension
import com.diffplug.gradle.spotless.KotlinExtension
import com.diffplug.gradle.spotless.KotlinGradleExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyAll
import io.mockk.verifyOrder
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginManager
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FormatterPluginTest {

    private lateinit var project: Project

    private lateinit var testSubject: FormatterPlugin

    @BeforeEach
    fun setup() {
        project = mockk()

        testSubject = FormatterPlugin()
    }

    @Test
    fun plugin_implements_contract() {
        assertInstanceOf(
            FormatterContract.Plugin::class.java,
            testSubject
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
        verify { pluginManager.apply("com.diffplug.spotless") }

        confirmVerified(pluginManager)
    }

    @Test
    fun `GIVEN project WHEN addRepository() THEN mavenCentral available`() {
        // GIVEN
        val repositoryHandler: RepositoryHandler = mockk(relaxed = true)
        every { project.repositories } returns repositoryHandler

        // WHEN
        testSubject.addRepository(project)

        // THEN
        verifyAll {
            repositoryHandler.mavenCentral()
        }

        confirmVerified(repositoryHandler)
    }

    @Test
    fun `GIVEN project WHEN configureFormatter() THEN formatter configured`() {
        // GIVEN
        val extensionContainer: ExtensionContainer = mockk()
        val spotlessExtension: SpotlessExtension = mockk()
        val kotlinExtension: KotlinExtension = mockk(relaxed = true)
        val kotlinGradleExtension: KotlinGradleExtension = mockk(relaxed = true)
        val markdownFormatExtension: FormatExtension = mockk(relaxed = true)
        val miscFormatExtension: FormatExtension = mockk(relaxed = true)

        every { project.extensions } returns extensionContainer
        every { extensionContainer.configure(SpotlessExtension::class.java, any()) } answers {
            secondArg<Action<SpotlessExtension>>().execute(spotlessExtension)
        }
        every { spotlessExtension.kotlin(any()) } answers {
            firstArg<Action<KotlinExtension>>().execute(kotlinExtension)
        }
        every { spotlessExtension.kotlinGradle(any()) } answers {
            firstArg<Action<KotlinGradleExtension>>().execute(kotlinGradleExtension)
        }
        every { spotlessExtension.format("markdown", any()) } answers {
            secondArg<Action<FormatExtension>>().execute(markdownFormatExtension)
        }
        every { spotlessExtension.format("misc", any()) } answers {
            secondArg<Action<FormatExtension>>().execute(miscFormatExtension)
        }

        // WHEN
        testSubject.configureFormatter(project)

        // THEN
        verifyAll {
            extensionContainer.configure(SpotlessExtension::class.java, any())

            spotlessExtension.kotlin(any())
            spotlessExtension.kotlinGradle(any())
            spotlessExtension.format("markdown", any())
            spotlessExtension.format("misc", any())

            kotlinExtension.ktlint()
            kotlinExtension.target("**/*.kt")
            kotlinExtension.targetExclude("**/build/", "**/resources/")
            kotlinExtension.trimTrailingWhitespace()
            kotlinExtension.indentWithSpaces()
            kotlinExtension.endWithNewline()

            kotlinGradleExtension.target("**/*.gradle.kts", "**/*.df.kts")
            kotlinGradleExtension.trimTrailingWhitespace()
            kotlinGradleExtension.indentWithSpaces()
            kotlinGradleExtension.endWithNewline()

            markdownFormatExtension.prettier()
            markdownFormatExtension.target("**/*.md")
            markdownFormatExtension.trimTrailingWhitespace()
            markdownFormatExtension.indentWithSpaces()
            markdownFormatExtension.endWithNewline()

            miscFormatExtension.target("**/.gitignore", "**/.gitattributes", ".java-version")
            miscFormatExtension.trimTrailingWhitespace()
            miscFormatExtension.indentWithSpaces()
            miscFormatExtension.endWithNewline()
        }

        confirmVerified(
            extensionContainer,
            spotlessExtension,
            kotlinExtension,
            kotlinGradleExtension,
            markdownFormatExtension,
            miscFormatExtension
        )
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
            testSubject.addRepository(project)
            testSubject.configureFormatter(project)
        }

        confirmVerified(
            spyTestSubject
        )
    }
}
