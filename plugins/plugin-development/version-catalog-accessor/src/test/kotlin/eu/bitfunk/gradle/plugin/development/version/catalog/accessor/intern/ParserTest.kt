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

import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Bundles
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Libraries
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Plugins
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Versions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ParserTest {

    private lateinit var parser: Parser

    @BeforeEach
    fun setup() {
        parser = Parser()
    }

    @Test
    fun `parser implements contract`() {
        assertInstanceOf(
            InternalContract.Parser::class.java,
            parser
        )
    }

    @Test
    fun `GIVEN version definitions WHEN parse() THEN return list of versions`() {
        // GIVEN
        val toml = """
            [versions]
            version = "1.0.0"
            group-version = "2.0.0"
        """.trimIndent()

        // WHEN
        val result = parser.parse(toml.byteInputStream())

        // THEN
        assertInstanceOf(Versions::class.java, result.versions)
        assertEquals(
            listOf(
                "version",
                "group-version"
            ),
            result.versions.items
        )
    }

    @Test
    fun `GIVEN library definitions WHEN parse() THEN return list of libraries`() {
        // GIVEN
        val toml = """
            [libraries]
            library = "com.fixture.library:simple:1.0.0"
            group-library = "com.fixture.library:group:2.0.0"
            library-complex = { module = "com.fixture.library:complex", version = "1.0.0" }
        """.trimIndent()

        // WHEN
        val result = parser.parse(toml.byteInputStream())

        // THEN
        assertInstanceOf(Libraries::class.java, result.libraries)
        assertEquals(
            listOf(
                "library",
                "group-library",
                "library-complex"
            ),
            result.libraries.items
        )
    }

    @Test
    fun `GIVEN bundle definitions WHEN parse() THEN return list of bundles`() {
        // GIVEN
        val toml = """
            [bundles]
            bundle = ["library1", "library2"]
        """.trimIndent()

        // WHEN
        val result = parser.parse(toml.byteInputStream())

        // THEN
        assertInstanceOf(Bundles::class.java, result.bundles)
        assertEquals(
            listOf(
                "bundle"
            ),
            result.bundles.items
        )
    }

    @Test
    fun `GIVEN plugin definitions WHEN parse() THEN return list of plugins`() {
        // GIVEN
        val toml = """
            [plugins]
            plugin = "com.fixture.plugin.simple:1.0.0"
            pluginComplex = { id = "com.fixture.plugin.complex", version = "2.0.0" }
        """.trimIndent()

        // WHEN
        val result = parser.parse(toml.byteInputStream())

        // THEN
        assertInstanceOf(Plugins::class.java, result.plugins)
        assertEquals(
            listOf(
                "plugin",
                "pluginComplex"
            ),
            result.plugins.items
        )
    }
}
