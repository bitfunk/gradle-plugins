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
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.PluginManager
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.util.GradleVersion
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

public class VersionCatalogHelperPluginTest {

    private lateinit var project: Project

    private lateinit var plugin: VersionCatalogHelperPlugin

    @BeforeEach
    public fun setup() {
        project = mockk()

        plugin = VersionCatalogHelperPlugin()
    }

    @Test
    public fun `plugin implements contract`() {
        Assertions.assertInstanceOf(
            VersionCatalogHelperContract.Plugin::class.java,
            plugin
        )
    }

    @Test
    public fun `GIVEN Gradle version 7_1 WHEN apply() THEN throw GradleException`() {
        // GIVEN
        mockkStatic(GradleVersion::class)
        every { GradleVersion.current() } returns GradleVersion.version("7.1")

        // WHEN/THEN
        Assertions.assertThrowsExactly(
            GradleException::class.java,
            { plugin.apply(project) },
            "This plugin requires Gradle 7.2 or later"
        )

        unmockkAll()
    }

    @Test
    public fun `GIVEN rootProject different to project WHEN apply() THEN throw GradleException`() {
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
    public fun `GIVEN java-gradle-plugin missing WHEN apply() THEN throw GradleException`() {
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
    public fun `GIVEN project with plugin WHEN addExtension() THEN extension is added to project with defaults`() {
        // GIVEN
        every { project.rootProject } returns project
        val extensions: ExtensionContainer = mockk()
        every { project.extensions } returns extensions
        val extension: VersionCatalogHelperPluginExtension = mockk(relaxed = true)
        every {
            extensions.create(any(), VersionCatalogHelperPluginExtension::class.java)
        } returns extension

        // WHEN
        plugin.addExtension(project)

        // THEN
        verify { extensions.create("versionCatalogHelper", VersionCatalogHelperPluginExtension::class.java) }
        verify { extension.catalogSourceFolder.convention("gradle/") }
        verify { extension.catalogNames.convention(listOf("libs")) }
        verify { extension.packageName.convention("") }
    }

    @Test
    public fun `GIVEN project and extension WHEN addHelperGeneratorTask() THEN task is registered and configured`() {
        // GIVEN
        val project = ProjectBuilder.builder().build()
        val extension = spyk(plugin.addExtension(project))

        // WHEN
        val task = plugin.addSourceGeneratorTask(project, extension)

        // THEN
        assertEquals("generateVersionCatalogHelperSource", task.name)
        assertEquals("gradle/", task.catalogSourceFolder.get())
        assertEquals(listOf("libs"), task.catalogNames.get())
        assertEquals("", task.packageName.get())
        verify { extension.catalogSourceFolder }
        verify { extension.catalogNames }
        verify { extension.packageName }
    }

    @Test
    public fun `GIVEN project and extension WHEN addCopySourceTask() THEN task is registered and configured`() {
        // GIVEN
        val project = ProjectBuilder.builder().build()
        val extension = spyk(plugin.addExtension(project))

        // WHEN
        val task = plugin.addSourceCopyTask(project, extension)

        // THEN
        assertEquals("copyVersionCatalogHelperSource", task.name)
    }

    @Test
    public fun `GIVEN project and extension WHEN addGeneratorTask() THEN task is registered and configured`() {
        // GIVEN
        val project = ProjectBuilder.builder().build()
        val extension = spyk(plugin.addExtension(project))

        // WHEN
        val task = plugin.addGeneratorTask(project)

        // THEN
        assertEquals("generateVersionCatalogHelper", task.name)
    }

    @Test
    public fun `GIVEN project WHEN configureSourceSet() THEN sourceSet configured`() {
        // GIVEN
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("java-gradle-plugin")

        // WHEN
        plugin.configureSourceSet(project)

        // THEN
        val javaExtension = project.extensions.getByName("java") as JavaPluginExtension
        val srcDirs = javaExtension.sourceSets.named("main").get().java.srcDirs
        assertEquals(2, srcDirs.size)
        assertEquals(
            "${project.buildDir}/generated/versionCatalogHelper/src/main/kotlin",
            "${srcDirs.toList()[1]}"
        )
    }

    @Test
    public fun `GIVEN project WHEN apply() THEN everything is configured`() {
        // GIVEN
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("java-gradle-plugin")

        // WHEN
        plugin.apply(project)

        // THEN
        val extension = project.extensions.findByName("versionCatalogHelper")
        assertNotNull(extension)
        assertInstanceOf(VersionCatalogHelperPluginExtension::class.java, extension)

        assertEquals(1, project.getTasksByName("generateVersionCatalogHelperSource", false).size)
        assertEquals(1, project.getTasksByName("copyVersionCatalogHelperSource", false).size)
        assertEquals(1, project.getTasksByName("generateVersionCatalogHelper", false).size)

        val javaExtension = project.extensions.getByName("java") as JavaPluginExtension
        val srcDirs = javaExtension.sourceSets.named("main").get().java.srcDirs
        assertEquals(2, srcDirs.size)
        assertEquals(
            "${project.buildDir}/generated/versionCatalogHelper/src/main/kotlin",
            "${srcDirs.toList()[1]}"
        )
    }
}
