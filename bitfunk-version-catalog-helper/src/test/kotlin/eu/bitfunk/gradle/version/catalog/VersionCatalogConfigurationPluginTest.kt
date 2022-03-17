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

package eu.bitfunk.gradle.version.catalog

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.Convention
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.typeOf
import org.gradle.util.GradleVersion
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VersionCatalogConfigurationPluginTest {

    lateinit var project: Project

    lateinit var plugin: VersionCatalogConfigurationPlugin

    @BeforeEach
    fun setup() {
        project = mockk()

        plugin = VersionCatalogConfigurationPlugin()
    }

    @Test
    fun `plugin implements contract`() {
        Assertions.assertInstanceOf(
            VersionCatalogHelperContract.Plugin::class.java,
            plugin
        )
    }

    @Test
    fun `GIVEN Gradle version 7_1 WHEN apply() THEN throw GradleException`() {
        // GIVEN
        mockkStatic(GradleVersion::class)
        every { GradleVersion.current() } returns GradleVersion.version("7.1")

        // WHEN/THEN
        Assertions.assertThrowsExactly(
            GradleException::class.java,
            { plugin.apply(project) },
            "This plugin requires Gradle 7.2 or later"
        )
    }

    @Test
    fun `GIVEN rootProject different to project WHEN apply() THEN throw GradleException`() {
        // GIVEN
        val newProject: Project = mockk()
        every { project.rootProject } returns newProject

        // WHEN/THEN
        Assertions.assertThrowsExactly(
            GradleException::class.java,
            { plugin.apply(project) },
            "This plugin should be applied to root project only"
        )
    }

    @Test
    fun `GIVEN java-gradle-plugin missing WHEN apply() THEN throw GradleException`() {
        // GIVEN
        every { project.rootProject } returns project
        val pluginManager: PluginManager = mockk()
        every { project.pluginManager } returns pluginManager
        every { pluginManager.hasPlugin("java-gradle-plugin") } returns false

        // WHEN/THEN
        Assertions.assertThrowsExactly(
            GradleException::class.java,
            { plugin.apply(project) },
            "The VersionCatalogHelperPlugin requires the `java-gradle-plugin` to work."
        )
    }

    @Test
    fun `GIVEN extension WHEN apply() THEN extension is added to project with defaults`() {
        // GIVEN
        every { project.rootProject } returns project
        val pluginManager: PluginManager = mockk()
        every { project.pluginManager } returns pluginManager
        every { pluginManager.hasPlugin("java-gradle-plugin") } returns true
        val extensions: ExtensionContainer = mockk(relaxed = true)
        every { project.extensions } returns extensions
        val convention: Convention = mockk(relaxed = true)
        every { project.convention } returns convention
        val extension: VersionCatalogConfigurationPluginExtension = mockk(relaxed = true)
        every { convention.findByType(typeOf<VersionCatalogConfigurationPluginExtension>()) } returns extension

        // WHEN
        plugin.apply(project)

        // THEN
        verify { extensions.create("versionCatalogHelper", VersionCatalogConfigurationPluginExtension::class.java) }
        verify { extension.catalogSourceFolder.set("gradle/") }
        verify { extension.catalogNames.set(listOf("libs")) }
        verify { extension.packageName.set("") }
    }
}
