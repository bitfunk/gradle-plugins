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

package eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern

import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.Catalog
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Bundles
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Libraries
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Plugins
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Versions
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.Node
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class GeneratorTaskTest {

    @TempDir
    private lateinit var projectRootDir: File

    @TempDir
    private lateinit var projectBuildDir: File

    private lateinit var mapper: InternalContract.Mapper
    private lateinit var parser: InternalContract.Parser

    private lateinit var generatorTask: GeneratorTask

    @BeforeEach
    fun setup() {
        mapper = mockk()
        parser = mockk()

        generatorTask = GeneratorTask(
            projectRootDir,
            projectBuildDir,
            mapper,
            parser
        )
    }

    @Test
    fun `generatorTask implements contract`() {
        assertInstanceOf(
            InternalContract.GeneratorTask::class.java,
            generatorTask
        )
    }

    @Test
    fun `GIVEN empty catalogNames WHEN generate() THEN do nothing`() {
        // GIVEN
        val catalogSourceFolder = ""
        val packageName = ""
        val catalogNames: List<String> = emptyList()

        // WHEN
        generatorTask.generate(catalogSourceFolder, packageName, catalogNames)

        // THEN
        confirmVerified(mapper, parser)
    }

    @Test
    fun `GIVEN catalogNames WHEN generate() THEN accessor present in output folder`() {
        // GIVEN
        val catalogSourceFolder = ""
        val packageName = ""
        val catalogNames: List<String> = listOf("libs", "deps")
        val outputFolder = File("$projectBuildDir/generated/versionCatalogAccessor/src/main/kotlin")

        File(projectRootDir, "libs.versions.toml").writeText("libs")
        File(projectRootDir, "deps.versions.toml").writeText("deps")

        every { parser.parse(any()) } returns CATALOG

        val nodes: List<Node> = emptyList()
        every { mapper.map(any()) } returns nodes

        // WHEN
        generatorTask.generate(catalogSourceFolder, packageName, catalogNames)

        // THEN
        verify { parser.parse(any()) }
        verify { mapper.map(any()) }

        confirmVerified(parser, mapper)

        val versionCatalogDependencyFile = File("$outputFolder/VersionCatalogDependency.kt")
        val libsAccessorFile = File("$outputFolder/LibsVersionCatalogAccessor.kt")
        val depsAccessorFile = File("$outputFolder/DepsVersionCatalogAccessor.kt")

        assertTrue(outputFolder.exists())
        assertEquals(3, outputFolder.listFiles()!!.size)
        assertTrue(versionCatalogDependencyFile.exists())
        assertTrue(libsAccessorFile.exists())
        assertTrue(depsAccessorFile.exists())
    }

    @Test
    fun `GIVEN catalogNames and sourceFolder WHEN generate() THEN accessor present in output folder`() {
        // GIVEN
        val catalogSourceFolder = "catalogs"
        val packageName = ""
        val catalogNames: List<String> = listOf("libs", "deps")
        val outputFolder = File("$projectBuildDir/generated/versionCatalogAccessor/src/main/kotlin")

        val catalogFolder = File("$projectRootDir/catalogs")
        catalogFolder.mkdir()
        File(catalogFolder, "libs.versions.toml").writeText("libs")
        File(catalogFolder, "deps.versions.toml").writeText("deps")

        every { parser.parse(any()) } returns CATALOG

        val nodes: List<Node> = emptyList()
        every { mapper.map(any()) } returns nodes

        // WHEN
        generatorTask.generate(catalogSourceFolder, packageName, catalogNames)

        // THEN
        verify { parser.parse(any()) }
        verify { mapper.map(any()) }

        confirmVerified(parser, mapper)

        val versionCatalogDependencyFile = File("$outputFolder/VersionCatalogDependency.kt")
        val libsAccessorFile = File("$outputFolder/LibsVersionCatalogAccessor.kt")
        val depsAccessorFile = File("$outputFolder/DepsVersionCatalogAccessor.kt")

        assertTrue(outputFolder.exists())
        assertEquals(3, outputFolder.listFiles()!!.size)
        assertTrue(versionCatalogDependencyFile.exists())
        assertTrue(libsAccessorFile.exists())
        assertTrue(depsAccessorFile.exists())
    }

    @Test
    fun `GIVEN catalogNames, sourceFolder, packageName WHEN generate() THEN all present in output`() {
        // GIVEN
        val catalogSourceFolder = "catalogs"
        val packageName = "com.example"
        val catalogNames: List<String> = listOf("libs", "deps")
        val outputFolder = File("$projectBuildDir/generated/versionCatalogAccessor/src/main/kotlin")

        val catalogFolder = File("$projectRootDir/catalogs")
        catalogFolder.mkdir()
        File(catalogFolder, "libs.versions.toml").writeText("libs")
        File(catalogFolder, "deps.versions.toml").writeText("deps")

        every { parser.parse(any()) } returns CATALOG

        val nodes: List<Node> = emptyList()
        every { mapper.map(any()) } returns nodes

        // WHEN
        generatorTask.generate(catalogSourceFolder, packageName, catalogNames)

        // THEN
        verify { parser.parse(any()) }
        verify { mapper.map(any()) }

        confirmVerified(parser, mapper)

        val versionCatalogDependencyFile = File("$outputFolder/VersionCatalogDependency.kt")
        val libsAccessorFile = File("$outputFolder/LibsVersionCatalogAccessor.kt")
        val depsAccessorFile = File("$outputFolder/DepsVersionCatalogAccessor.kt")

        assertTrue(outputFolder.exists())
        assertEquals(3, outputFolder.listFiles()!!.size)
        assertTrue(versionCatalogDependencyFile.exists())
        assertTrue(versionCatalogDependencyFile.readText().contains(packageName))
        assertTrue(versionCatalogDependencyFile.readText().contains("VersionCatalogDependency"))
        assertTrue(libsAccessorFile.exists())
        assertTrue(libsAccessorFile.readText().contains(packageName))
        assertTrue(libsAccessorFile.readText().contains("LibsVersionCatalogAccessor"))
        assertTrue(depsAccessorFile.exists())
        assertTrue(depsAccessorFile.readText().contains(packageName))
        assertTrue(depsAccessorFile.readText().contains("DepsVersionCatalogAccessor"))
    }

    private companion object {
        val CATALOG = Catalog(
            Versions(emptyMap()),
            Libraries(emptyList()),
            Bundles(emptyList()),
            Plugins(emptyList()),
        )
    }
}
