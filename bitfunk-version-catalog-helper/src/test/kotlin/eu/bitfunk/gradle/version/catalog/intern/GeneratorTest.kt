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

import eu.bitfunk.gradle.version.catalog.VersionCatalogHelperContract.Mapper
import eu.bitfunk.gradle.version.catalog.intern.model.Catalog
import eu.bitfunk.gradle.version.catalog.intern.model.CatalogEntry.Bundles
import eu.bitfunk.gradle.version.catalog.intern.model.CatalogEntry.Libraries
import eu.bitfunk.gradle.version.catalog.intern.model.CatalogEntry.Plugins
import eu.bitfunk.gradle.version.catalog.intern.model.CatalogEntry.Versions
import eu.bitfunk.gradle.version.catalog.intern.model.Node
import eu.bitfunk.gradle.version.catalog.intern.test.FileHelper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GeneratorTest {

    lateinit var mapper: Mapper

    @BeforeEach
    fun setup() {
        mapper = mockk()
        every { mapper.map(emptyList()) } returns emptyList()
    }

    @Test
    fun `GIVEN empty catalog WHEN generate() THEN return empty helper`() {
        // GIVEN
        val baseName = "Empty"
        val catalog = Catalog(
            Versions(emptyList()),
            Libraries(emptyList()),
            Bundles(emptyList()),
            Plugins(emptyList()),
        )
        val generator = Generator(PACKAGE_NAME, baseName, mapper)

        // WHEN
        val result = generator.generate(catalog)

        // THEN
        val expected = FileHelper.loadAsString("fixture/EmptyVersionCatalogHelper.kt")
        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `GIVEN catalog with versions WHEN generate() THEN return helper with versions`() {
        // GIVEN
        val baseName = "WithVersions"
        val catalog = Catalog(
            Versions(TEST_ITEMS),
            Libraries(emptyList()),
            Bundles(emptyList()),
            Plugins(emptyList()),
        )
        val generator = Generator(PACKAGE_NAME, baseName, mapper)
        every { mapper.map(TEST_ITEMS) } returns TEST_NODE_LIST

        // WHEN
        val result = generator.generate(catalog)

        // THEN
        val expected = FileHelper.loadAsString("fixture/WithVersionsVersionCatalogHelper.kt")
        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `GIVEN catalog with libraries WHEN generate() THEN return helper with libraries`() {
        // GIVEN
        val baseName = "WithLibraries"
        val catalog = Catalog(
            Versions(emptyList()),
            Libraries(TEST_ITEMS),
            Bundles(emptyList()),
            Plugins(emptyList()),
        )
        val generator = Generator(PACKAGE_NAME, baseName, mapper)
        every { mapper.map(TEST_ITEMS) } returns TEST_NODE_LIST

        // WHEN
        val result = generator.generate(catalog)

        // THEN
        val expected = FileHelper.loadAsString("fixture/WithLibrariesVersionCatalogHelper.kt")
        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `GIVEN catalog with bundles WHEN generate() THEN return helper with bundles`() {
        // GIVEN
        val baseName = "WithBundles"
        val catalog = Catalog(
            Versions(emptyList()),
            Libraries(emptyList()),
            Bundles(TEST_ITEMS),
            Plugins(emptyList()),
        )
        val generator = Generator(PACKAGE_NAME, baseName, mapper)
        every { mapper.map(TEST_ITEMS) } returns TEST_NODE_LIST

        // WHEN
        val result = generator.generate(catalog)

        // THEN
        val expected = FileHelper.loadAsString("fixture/WithBundlesVersionCatalogHelper.kt")
        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `GIVEN catalog with plugins WHEN generate() THEN return helper with plugins`() {
        // GIVEN
        val baseName = "WithPlugins"
        val catalog = Catalog(
            Versions(emptyList()),
            Libraries(emptyList()),
            Bundles(emptyList()),
            Plugins(TEST_ITEMS),
        )
        val generator = Generator(PACKAGE_NAME, baseName, mapper)
        every { mapper.map(TEST_ITEMS) } returns TEST_NODE_LIST

        // WHEN
        val result = generator.generate(catalog)

        // THEN
        val expected = FileHelper.loadAsString("fixture/WithPluginsVersionCatalogHelper.kt")
        Assertions.assertEquals(expected, result)
    }

    companion object {
        const val PACKAGE_NAME = "com.example.catalog"

        val TEST_ITEMS = listOf(
            "example",
            "group-example",
            "group-example-one",
            "group-example-two",
        )

        val TEST_NODE_LIST = listOf(
            Node("example", "example"),
            Node(
                name = "group", children = mutableListOf(
                    Node(
                        "example", "group-example", mutableListOf(
                            Node("one", "group-example-one"),
                            Node("two", "group-example-two"),
                        )
                    )
                )
            ),
        )
    }
}