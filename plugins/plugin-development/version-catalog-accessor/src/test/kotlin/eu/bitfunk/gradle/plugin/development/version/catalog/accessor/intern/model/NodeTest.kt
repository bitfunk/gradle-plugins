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

package eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class NodeTest {

    @Test
    fun `GIVEN name WHEN initialized THEN defaults set`() {
        // GIVEN
        val name = "Name"

        // WHEN
        val result = Node(name = name)

        // THEN
        assertEquals("Name", result.name)
        assertEquals("", result.path)
        assertEquals(mutableListOf<Node>(), result.children)
    }

    @Test
    fun `GIVEN name, path and children WHEN initialized THEN all values present`() {
        // GIVEN
        val name = "Name"
        val path = "Path"
        val children = mutableListOf(Node("Child"))

        // WHEN
        val result = Node(name = name, path = path, children = children)

        // THEN
        assertEquals("Name", result.name)
        assertEquals("Path", result.path)
        assertEquals(mutableListOf(Node("Child")), result.children)
    }

    @Test
    fun `GIVEN name and children WHEN initialized THEN is group`() {
        // GIVEN
        val name = "Name"
        val children = mutableListOf(Node("Child"))

        // WHEN
        val result = Node(name = name, children = children)

        // THEN
        assertTrue(result.isGroup())
    }

    @Test
    fun `GIVEN name WHEN child added THEN is group`() {
        // GIVEN
        val node = Node(name = "Name")
        val child = Node(name = "Child")

        // WHEN
        val initial = node.isGroup()

        node.children.add(child)
        val childAdded = node.isGroup()

        // THEN
        assertFalse(initial)
        assertTrue(childAdded)
    }

    @Test
    fun `GIVEN name and path WHEN initialized THEN is leaf`() {
        // GIVEN
        val name = "Name"
        val path = "Path"
        val node = Node(name = "Name", path = "Path")

        // WHEN
        val result = Node(name = name, path = path)

        // THEN
        assertTrue(result.isLeaf())
        assertEquals(node, result)
    }

    @Test
    fun `GIVEN name WHEN path and child added THEN isLeaf is correct`() {
        // GIVEN
        val node = Node(name = "Name")
        val path = "Path"
        val child = Node(name = "Child")

        // WHEN
        val initial = node.isLeaf()

        node.path = path
        val pathAdded = node.isLeaf()

        node.children.add(child)
        val childAdded = node.isLeaf()

        // THEN
        assertFalse(initial)
        assertTrue(pathAdded)
        assertTrue(childAdded)
    }

    @Test
    fun `GIVEN name, path and children WHEN initialized THEN is lead and group`() {
        // GIVEN
        val name = "Name"
        val path = "Path"
        val children = mutableListOf(Node("Child"))

        // WHEN
        val result = Node(name, path, children)

        // THEN
        assertTrue(result.isLeaf())
        assertTrue(result.isGroup())
    }
}
