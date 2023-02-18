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
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.test.FileHelper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GeneratorTest {

    private lateinit var mapper: Mapper

    @BeforeEach
    fun setup() {
        mapper = mockk()
        every { mapper.map(emptyMap()) } returns emptyList()
    }

    @Test
    fun `generator implements contract`() {
        val generator = Generator(PACKAGE_NAME, "", mapper)

        assertInstanceOf(
            InternalContract.Generator::class.java,
            generator,
        )
    }

    @Test
    fun `GIVEN empty catalog WHEN generate() THEN return empty accessor`() {
        // GIVEN
        val baseName = "empty"
        val catalog = Catalog(
            Versions(emptyMap()),
            Libraries(emptyList()),
            Bundles(emptyList()),
            Plugins(emptyList()),
        )
        val generator = Generator(PACKAGE_NAME, baseName, mapper)

        // WHEN
        val result = generator.generate(catalog)

        // THEN
        val expected = FileHelper.loadAsString("fixture/EmptyVersionCatalogAccessor.kt")
        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN catalog with versions WHEN generate() THEN return accessor with versions`() {
        // GIVEN
        val baseName = "with-versions"
        val catalog = Catalog(
            Versions(TEST_VERSIONS),
            Libraries(emptyList()),
            Bundles(emptyList()),
            Plugins(emptyList()),
        )
        val generator = Generator(PACKAGE_NAME, baseName, mapper)
        every { mapper.map(TEST_VERSIONS) } returns TEST_VERSION_NODES

        // WHEN
        val result = generator.generate(catalog)

        // THEN
        val expected = FileHelper.loadAsString("fixture/WithVersionsVersionCatalogAccessor.kt")
        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN catalog with libraries WHEN generate() THEN return accessor with libraries`() {
        // GIVEN
        val baseName = "with-libraries"
        val catalog = Catalog(
            Versions(emptyMap()),
            Libraries(TEST_ITEMS),
            Bundles(emptyList()),
            Plugins(emptyList()),
        )
        val generator = Generator(PACKAGE_NAME, baseName, mapper)
        every { mapper.map(TEST_ITEM_MAP) } returns TEST_NODE_LIST

        // WHEN
        val result = generator.generate(catalog)

        // THEN
        val expected = FileHelper.loadAsString("fixture/WithLibrariesVersionCatalogAccessor.kt")
        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN catalog with bundles WHEN generate() THEN return accessor with bundles`() {
        // GIVEN
        val baseName = "with-bundles"
        val catalog = Catalog(
            Versions(emptyMap()),
            Libraries(emptyList()),
            Bundles(TEST_ITEMS),
            Plugins(emptyList()),
        )
        val generator = Generator(PACKAGE_NAME, baseName, mapper)
        every { mapper.map(TEST_ITEM_MAP) } returns TEST_NODE_LIST

        // WHEN
        val result = generator.generate(catalog)

        // THEN
        val expected = FileHelper.loadAsString("fixture/WithBundlesVersionCatalogAccessor.kt")
        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN catalog with plugins WHEN generate() THEN return accessor with plugins`() {
        // GIVEN
        val baseName = "with-plugins"
        val catalog = Catalog(
            Versions(emptyMap()),
            Libraries(emptyList()),
            Bundles(emptyList()),
            Plugins(TEST_ITEMS),
        )
        val generator = Generator(PACKAGE_NAME, baseName, mapper)
        every { mapper.map(TEST_ITEM_MAP) } returns TEST_NODE_LIST

        // WHEN
        val result = generator.generate(catalog)

        // THEN
        val expected = FileHelper.loadAsString("fixture/WithPluginsVersionCatalogAccessor.kt")
        assertEquals(expected, result)
    }

    private companion object {
        const val PACKAGE_NAME = "com.example.catalog"

        val TEST_VERSIONS = mapOf(
            "example" to "1.0.0",
            "group-example" to "4.5.9",
            "group-example-one" to "1.2.3",
            "group-example-two" to "0.1.0",
        )

        val TEST_VERSION_NODES = listOf(
            Node("example", "example", "1.0.0"),
            Node(
                name = "group",
                children = mutableListOf(
                    Node(
                        "example",
                        "group-example",
                        value = "4.5.9",
                        mutableListOf(
                            Node("one", "group-example-one", "1.2.3"),
                            Node("two", "group-example-two", "0.1.0"),
                        ),
                    ),
                ),
            ),
        )

        val TEST_ITEMS = listOf(
            "example",
            "group-example",
            "group-example-one",
            "group-example-two",
        )

        val TEST_ITEM_MAP = mapOf(
            "example" to null,
            "group-example" to null,
            "group-example-one" to null,
            "group-example-two" to null,
        )

        val TEST_NODE_LIST = listOf(
            Node("example", "example"),
            Node(
                name = "group",
                children = mutableListOf(
                    Node(
                        "example",
                        "group-example",
                        null,
                        mutableListOf(
                            Node("one", "group-example-one"),
                            Node("two", "group-example-two"),
                        ),
                    ),
                ),
            ),
        )
    }
}
