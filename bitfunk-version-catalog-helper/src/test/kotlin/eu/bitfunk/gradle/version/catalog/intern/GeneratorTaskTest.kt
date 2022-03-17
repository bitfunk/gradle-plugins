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

package eu.bitfunk.gradle.version.catalog.intern

import eu.bitfunk.gradle.version.catalog.VersionCatalogHelperContract
import eu.bitfunk.gradle.version.catalog.intern.GeneratorTest.Companion
import eu.bitfunk.gradle.version.catalog.intern.model.Catalog
import eu.bitfunk.gradle.version.catalog.intern.model.CatalogEntry.Bundles
import eu.bitfunk.gradle.version.catalog.intern.model.CatalogEntry.Libraries
import eu.bitfunk.gradle.version.catalog.intern.model.CatalogEntry.Plugins
import eu.bitfunk.gradle.version.catalog.intern.model.CatalogEntry.Versions
import eu.bitfunk.gradle.version.catalog.intern.model.Node
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class GeneratorTaskTest {

    @TempDir
    lateinit var projectRootDir: File

    @TempDir
    lateinit var projectBuildDir: File

    lateinit var mapper: VersionCatalogHelperContract.Mapper
    lateinit var parser: VersionCatalogHelperContract.Parser

    lateinit var generatorTask: GeneratorTask

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
        Assertions.assertInstanceOf(
            VersionCatalogHelperContract.Task.Generator.Intern::class.java,
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
    fun `GIVEN catalogNames WHEN generate() THEN helper present in output folder`() {
        // GIVEN
        val catalogSourceFolder = ""
        val packageName = ""
        val catalogNames: List<String> = listOf("libs", "deps")
        val outputFolder = File("$projectBuildDir/generated/versionCatalogHelper/src/main/kotlin")

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

        val libsHelperFile = File("${outputFolder}/LibsVersionCatalogHelper.kt")
        val depsHelperFile = File("${outputFolder}/DepsVersionCatalogHelper.kt")

        Assertions.assertTrue(outputFolder.exists())
        Assertions.assertTrue(libsHelperFile.exists())
        Assertions.assertTrue(depsHelperFile.exists())
    }

    @Test
    fun `GIVEN catalogNames and sourceFolder WHEN generate() THEN helper present in output folder`() {
        // GIVEN
        val catalogSourceFolder = "catalogs"
        val packageName = ""
        val catalogNames: List<String> = listOf("libs", "deps")
        val outputFolder = File("$projectBuildDir/generated/versionCatalogHelper/src/main/kotlin")

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

        val libsHelperFile = File("${outputFolder}/LibsVersionCatalogHelper.kt")
        val depsHelperFile = File("${outputFolder}/DepsVersionCatalogHelper.kt")

        Assertions.assertTrue(outputFolder.exists())
        Assertions.assertTrue(libsHelperFile.exists())
        Assertions.assertTrue(depsHelperFile.exists())
    }

    @Test
    fun `GIVEN catalogNames, sourceFolder, packageName WHEN generate() THEN all present in output`() {
        // GIVEN
        val catalogSourceFolder = "catalogs"
        val packageName = "com.example"
        val catalogNames: List<String> = listOf("libs", "deps")
        val outputFolder = File("$projectBuildDir/generated/versionCatalogHelper/src/main/kotlin")

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

        val libsHelperFile = File("${outputFolder}/LibsVersionCatalogHelper.kt")
        val depsHelperFile = File("${outputFolder}/DepsVersionCatalogHelper.kt")

        Assertions.assertTrue(outputFolder.exists())
        Assertions.assertTrue(libsHelperFile.exists())
        Assertions.assertTrue(libsHelperFile.readText().contains(packageName))
        Assertions.assertTrue(depsHelperFile.exists())
        Assertions.assertTrue(depsHelperFile.readText().contains(packageName))
    }

    companion object {
        private val CATALOG = Catalog(
            Versions(emptyList()),
            Libraries(emptyList()),
            Bundles(emptyList()),
            Plugins(emptyList()),
        )
    }
}
