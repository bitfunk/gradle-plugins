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

package eu.bitfunk.gradle.plugin.development.convention

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.junit.jupiter.api.Test

class GradleExtensionsKtTest {

    @Test
    fun `GIVEN project WHEN javaPlugin() THEN extension configured`() {
        // GIVEN
        val project: Project = mockk()
        val extensionContainer: ExtensionContainer = mockk(relaxed = true)
        val action: Action<JavaPluginExtension> = mockk()

        every { project.extensions } returns extensionContainer

        // WHEN
        project.javaPlugin(action)

        // THEN
        verify {
            project.extensions
            extensionContainer.configure(JavaPluginExtension::class.java, action)
        }

        confirmVerified(project, extensionContainer)
    }

    @Test
    @Suppress("UnstableApiUsage")
    fun `GIVEN project WHEN mavenPublishing() THEN extension configured`() {
        // GIVEN
        val project: Project = mockk()
        val extensionContainer: ExtensionContainer = mockk(relaxed = true)
        val action: Action<MavenPublishBaseExtension> = mockk()

        every { project.extensions } returns extensionContainer

        // WHEN
        project.mavenPublishing(action)

        // THEN
        verify {
            project.extensions
            extensionContainer.configure(MavenPublishBaseExtension::class.java, action)
        }

        confirmVerified(project, extensionContainer)
    }

    @Test
    fun `GIVEN project WHEN jacoco() THEN extension configured`() {
        // GIVEN
        val project: Project = mockk()
        val extensionContainer: ExtensionContainer = mockk(relaxed = true)
        val action: Action<JacocoPluginExtension> = mockk()

        every { project.extensions } returns extensionContainer

        // WHEN
        project.jacoco(action)

        // THEN
        verify {
            project.extensions
            extensionContainer.configure(JacocoPluginExtension::class.java, action)
        }

        confirmVerified(project, extensionContainer)
    }

    @Test
    fun `GIVEN dependencyHandler WHEN implementation() THEN added`() {
        // GIVEN
        val dependencyHandler: DependencyHandler = mockk(relaxed = true)
        val dependencyNotation: Any = mockk()

        // WHEN
        dependencyHandler.implementation(dependencyNotation)

        // THEN
        verify {
            dependencyHandler.add("implementation", dependencyNotation)
        }

        confirmVerified(dependencyHandler)
    }

    @Test
    fun `GIVEN dependencyHandler WHEN testImplementation() THEN added`() {
        // GIVEN
        val dependencyHandler: DependencyHandler = mockk(relaxed = true)
        val dependencyNotation: Any = mockk()

        // WHEN
        dependencyHandler.testImplementation(dependencyNotation)

        // THEN
        verify {
            dependencyHandler.add("testImplementation", dependencyNotation)
        }

        confirmVerified(dependencyHandler)
    }

    @Test
    fun `GIVEN dependencyHandler WHEN testRuntimeOnly() THEN added`() {
        // GIVEN
        val dependencyHandler: DependencyHandler = mockk(relaxed = true)
        val dependencyNotation: Any = mockk()

        // WHEN
        dependencyHandler.testRuntimeOnly(dependencyNotation)

        // THEN
        verify {
            dependencyHandler.add("testRuntimeOnly", dependencyNotation)
        }

        confirmVerified(dependencyHandler)
    }
}
