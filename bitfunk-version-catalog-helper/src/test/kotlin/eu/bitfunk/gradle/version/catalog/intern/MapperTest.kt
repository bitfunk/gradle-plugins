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
import eu.bitfunk.gradle.version.catalog.intern.model.Node
import org.junit.jupiter.api.Assertions

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MapperTest {

    private lateinit var mapper: VersionCatalogHelperContract.Mapper

    @BeforeEach
    fun setup() {
        mapper = Mapper()
    }

    @Test
    fun `mapper implements contract`() {
        Assertions.assertInstanceOf(
            VersionCatalogHelperContract.Mapper::class.java,
            mapper
        )
    }

    @Test
    fun `GIVEN empty list WHEN map() THEN return empty list`() {
        // GIVEN
        val input = emptyList<String>()

        // WHEN
        val result = mapper.map(input)

        // THEN
        Assertions.assertEquals(
            emptyList<Node>(),
            result
        )
    }

    @Test
    fun `GIVEN list with top level item WHEN map() THEN return top level list`() {
        // GIVEN
        val input = listOf("example")

        // WHEN
        val result = mapper.map(input)

        // THEN
        Assertions.assertEquals(
            listOf(Node("example", "example")),
            result
        )
    }

    @Test
    fun `GIVEN list with group item WHEN map() THEN return list with nested items`() {
        // GIVEN
        val input = listOf("group-example")

        // WHEN
        val result = mapper.map(input)

        // THEN
        Assertions.assertEquals(
            listOf(
                Node(
                    name = "group", children = mutableListOf(
                        Node("example", "group-example")
                    )
                ),
            ),
            result
        )
    }

    @Test
    fun `GIVEN list with multiple group items WHEN map() THEN return list with all nested items`() {
        // GIVEN
        val input = listOf("group-example-one", "group-example-two")

        // WHEN
        val result = mapper.map(input)

        // THEN
        Assertions.assertEquals(
            listOf(
                Node(
                    name = "group", children = mutableListOf(
                        Node(
                            name = "example", children = mutableListOf(
                                Node("one", "group-example-one"),
                                Node("two", "group-example-two")
                            )
                        )
                    )
                ),
            ),
            result
        )
    }

    @Test
    fun `GIVEN list with mixed items WHEN map() THEN return list with all nested items`() {
        // GIVEN
        val input = listOf("group", "group-example", "group-example-one", "group-example-two")

        // WHEN
        val result = mapper.map(input)

        // THEN
        Assertions.assertEquals(
            listOf(
                Node(
                    name = "group", path = "group", children = mutableListOf(
                        Node(
                            name = "example", path = "group-example", children = mutableListOf(
                                Node("one", "group-example-one"),
                                Node("two", "group-example-two")
                            )
                        )
                    )
                ),
            ),
            result
        )
    }
}
