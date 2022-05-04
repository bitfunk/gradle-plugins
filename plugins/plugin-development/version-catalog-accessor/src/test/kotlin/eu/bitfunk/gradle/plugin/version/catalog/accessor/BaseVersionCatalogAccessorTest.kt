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

package eu.bitfunk.gradle.plugin.version.catalog.accessor

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugin.use.PluginDependency
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional

class BaseVersionCatalogAccessorTest {

    private lateinit var versionCatalog: VersionCatalog

    private lateinit var accessor: TestVersionCatalogAccessor

    @BeforeEach
    fun setup() {
        val project: Project = mockk()
        val extensionContainer: ExtensionContainer = mockk()
        val versionCatalogsExtension: VersionCatalogsExtension = mockk()
        versionCatalog = mockk()

        every { project.extensions } returns extensionContainer
        every { extensionContainer.getByType(VersionCatalogsExtension::class) } returns versionCatalogsExtension
        every { versionCatalogsExtension.named("libs") } returns versionCatalog

        accessor = TestVersionCatalogAccessor(project)
    }

    @Test
    fun `GIVEN versionCatalogName WHEN initialized THEN use provided catalog name`() {
        // GIVEN
        val versionCatalogName = "versionCatalogName"
        val project: Project = mockk()
        val extensionContainer: ExtensionContainer = mockk()
        val versionCatalogsExtension: VersionCatalogsExtension = mockk()

        every { project.extensions } returns extensionContainer
        every { extensionContainer.getByType(VersionCatalogsExtension::class) } returns versionCatalogsExtension
        every { versionCatalogsExtension.named(versionCatalogName) } returns versionCatalog

        // WHEN
        TestVersionCatalogAccessor(project, versionCatalogName)

        // THEN
        verify { versionCatalogsExtension.named("versionCatalogName") }
    }

    @Test
    fun `GIVEN invalid version WHEN findVersion() THEN throw NoSuchElementException`() {
        // GIVEN
        val version = "invalid_version"

        // WHEN/THEN
        assertThrowsExactly(
            NoSuchElementException::class.java
        ) { accessor.testFindVersion(version) }
    }

    @Test
    fun `GIVEN versionName WHEN findVersion() THEN return version`() {
        // GIVEN
        val versionName = "versionName"

        val optional: Optional<VersionConstraint> = mockk()
        val versionConstraint: VersionConstraint = mockk()
        every { versionCatalog.findVersion(versionName) } returns optional
        every { optional.get() } returns versionConstraint
        every { versionConstraint.requiredVersion } returns "1.0.0"

        // WHEN
        val result = accessor.testFindVersion(versionName)

        // THEN
        assertEquals(
            "1.0.0",
            result
        )
    }

    @Test
    fun `GIVEN invalid library WHEN findLibrary() THEN throw NoSuchElementException`() {
        // GIVEN
        val library = "invalid_library"

        // WHEN/THEN
        assertThrowsExactly(
            NoSuchElementException::class.java
        ) { accessor.testFindLibrary(library) }
    }

    @Test
    fun `GIVEN libraryName WHEN findLibrary() THEN return library`() {
        // GIVEN
        val libraryName = "libraryName"

        val optional: Optional<Provider<MinimalExternalModuleDependency>> = mockk()
        val provider: Provider<MinimalExternalModuleDependency> = mockk()
        val moduleDependency: MinimalExternalModuleDependency = mockk()
        every { versionCatalog.findLibrary(libraryName) } returns optional
        every { optional.get() } returns provider
        every { provider.get() } returns moduleDependency
        every { moduleDependency.toString() } returns "com.example:library:1.0.0"

        // WHEN
        val result = accessor.testFindLibrary(libraryName)

        // THEN
        assertEquals(
            "com.example:library:1.0.0",
            result
        )
    }

    @Test
    fun `GIVEN invalid bundle WHEN findBundle() THEN throw NoSuchElementException`() {
        // GIVEN
        val bundle = "invalid_bundle"

        // WHEN/THEN
        assertThrowsExactly(
            NoSuchElementException::class.java
        ) { accessor.testFindBundle(bundle) }
    }

    @Test
    fun `GIVEN bundleName WHEN findBundle() THEN return bundle`() {
        // GIVEN
        val bundleName = "bundleName"

        val optional: Optional<Provider<ExternalModuleDependencyBundle>> = mockk()
        val provider: Provider<ExternalModuleDependencyBundle> = mockk()
        val moduleDependencyBundle: ExternalModuleDependencyBundle = mockk()
        every { versionCatalog.findBundle(bundleName) } returns optional
        every { optional.get() } returns provider
        every { provider.get() } returns moduleDependencyBundle
        every { moduleDependencyBundle.toString() } returns "bundle"

        // WHEN
        val result = accessor.testFindBundle(bundleName)

        // THEN
        assertEquals(
            "bundle",
            result
        )
    }

    @Test
    fun `GIVEN invalid plugin WHEN findPlugin() THEN throw NoSuchElementException`() {
        // GIVEN
        val plugin = "invalid_plugin"

        // WHEN/THEN
        assertThrowsExactly(
            NoSuchElementException::class.java
        ) { accessor.testFindPlugin(plugin) }
    }

    @Test
    fun `GIVEN pluginName WHEN findPlugin() THEN return plugin`() {
        // GIVEN
        val pluginName = "pluginName"

        val optional: Optional<Provider<PluginDependency>> = mockk()
        val provider: Provider<PluginDependency> = mockk()
        val pluginDependency: PluginDependency = mockk()
        every { versionCatalog.findPlugin(pluginName) } returns optional
        every { optional.get() } returns provider
        every { provider.get() } returns pluginDependency
        every { pluginDependency.toString() } returns "plugin"

        // WHEN
        val result = accessor.testFindPlugin(pluginName)

        // THEN
        assertEquals(
            "plugin",
            result
        )
    }

    private inner class TestVersionCatalogAccessor(
        project: Project,
        catalogName: String = DEFAULT_CATALOG_NAME
    ) : BaseVersionCatalogAccessor(project, catalogName) {

        fun testFindVersion(name: String): String {
            return findVersion(name)
        }

        fun testFindLibrary(name: String): String {
            return findLibrary(name)
        }

        fun testFindBundle(name: String): String {
            return findBundle(name)
        }

        fun testFindPlugin(name: String): String {
            return findPlugin(name)
        }
    }

    private companion object {
        const val DEFAULT_CATALOG_NAME = "libs"
    }
}
