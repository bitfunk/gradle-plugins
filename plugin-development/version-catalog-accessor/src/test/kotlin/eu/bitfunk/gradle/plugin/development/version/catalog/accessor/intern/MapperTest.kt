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

import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.Node
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MapperTest {

    private lateinit var mapper: InternalContract.Mapper

    @BeforeEach
    fun setup() {
        mapper = Mapper()
    }

    @Test
    fun `mapper implements contract`() {
        assertInstanceOf(
            InternalContract.Mapper::class.java,
            mapper
        )
    }

    @Test
    fun `GIVEN empty list WHEN map() THEN return empty list`() {
        // GIVEN
        val input = emptyMap<String, String?>()

        // WHEN
        val result = mapper.map(input)

        // THEN
        assertEquals(
            emptyList(),
            result
        )
    }

    @Test
    fun `GIVEN list with top level item WHEN map() THEN return top level list`() {
        // GIVEN
        val input = mapOf(
            "example" to null,
            "squad" to "1.0.0"
        )

        // WHEN
        val result = mapper.map(input)

        // THEN
        assertEquals(
            listOf(
                Node("example", "example"),
                Node("squad", "squad", "1.0.0")
            ),
            result
        )
    }

    @Test
    fun `GIVEN list with group item WHEN map() THEN return list with nested items`() {
        // GIVEN
        val input = mapOf(
            "group-example" to null,
            "squad-example" to "1.0.0"
        )

        // WHEN
        val result = mapper.map(input)

        // THEN
        assertEquals(
            listOf(
                Node(
                    name = "group",
                    children = mutableListOf(
                        Node("example", "group-example")
                    )
                ),
                Node(
                    name = "squad",
                    children = mutableListOf(
                        Node("example", "squad-example", "1.0.0")
                    )
                )
            ),
            result
        )
    }

    @Test
    fun `GIVEN list with multiple group items WHEN map() THEN return list with all nested items`() {
        // GIVEN
        val input = mapOf(
            "group-example-one" to null,
            "group-example-two" to null,
            "squad-example-one" to "1.0.0",
            "squad-example-two" to "2.0.0"
        )

        // WHEN
        val result = mapper.map(input)

        // THEN
        assertEquals(
            listOf(
                Node(
                    name = "group",
                    children = mutableListOf(
                        Node(
                            name = "example",
                            children = mutableListOf(
                                Node("one", "group-example-one"),
                                Node("two", "group-example-two")
                            )
                        )
                    )
                ),
                Node(
                    name = "squad",
                    children = mutableListOf(
                        Node(
                            name = "example",
                            children = mutableListOf(
                                Node("one", "squad-example-one", "1.0.0"),
                                Node("two", "squad-example-two", "2.0.0")
                            )
                        )
                    )
                )
            ),
            result
        )
    }

    @Test
    fun `GIVEN list with mixed items WHEN map() THEN return list with all nested items`() {
        // GIVEN
        val input = mapOf(
            "group" to null,
            "group-example" to null,
            "group-example-one" to null,
            "group-example-two" to null,
            "squad" to "3.0.0",
            "squad-example" to "4.0.0",
            "squad-example-one" to "1.0.0",
            "squad-example-two" to "2.0.0"
        )

        // WHEN
        val result = mapper.map(input)

        // THEN
        assertEquals(
            listOf(
                Node(
                    name = "group",
                    path = "group",
                    children = mutableListOf(
                        Node(
                            name = "example",
                            path = "group-example",
                            children = mutableListOf(
                                Node("one", "group-example-one"),
                                Node("two", "group-example-two")
                            )
                        )
                    )
                ),
                Node(
                    name = "squad",
                    path = "squad",
                    value = "3.0.0",
                    children = mutableListOf(
                        Node(
                            name = "example",
                            path = "squad-example",
                            value = "4.0.0",
                            children = mutableListOf(
                                Node("one", "squad-example-one", "1.0.0"),
                                Node("two", "squad-example-two", "2.0.0")
                            )
                        )
                    )
                )
            ),
            result
        )
    }
}
