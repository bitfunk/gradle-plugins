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

import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.InternalContract.Mapper
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.Node

internal class Mapper : Mapper {

    override fun map(items: List<String>): List<Node> {
        val nodes = mutableListOf<Node>()

        for (item in items) {
            if (item.contains(SEPARATOR)) {
                val newNode = mapToNodeData(item)

                val currentNode = nodes.find { it.name == newNode.name }

                if (currentNode != null) {
                    val updatedNode = updateNode(currentNode, newNode)
                    val currentNodeIndex = nodes.indexOf(currentNode)
                    nodes[currentNodeIndex] = updatedNode
                } else {
                    nodes.add(newNode)
                }
            } else {
                nodes.add(Node(item, item))
            }
        }

        return nodes
    }

    private fun mapToNodeData(path: String): Node {
        val splits = path.split(SEPARATOR)
        val lastIndex = splits.size - 1

        val nodes = path.split(SEPARATOR)
            .mapIndexed { index: Int, element: String ->
                if (index == lastIndex) {
                    Node(element, path)
                } else {
                    Node(element)
                }
            }.reversed()

        lateinit var node: Node

        for (i in nodes.indices) {
            if (i == 0) {
                node = nodes[i]
            } else {
                val tmpNode = nodes[i]
                tmpNode.children.add(node)
                node = tmpNode
            }
        }

        return node
    }

    private fun updateNode(currentNode: Node, newNode: Node): Node {
        if (newNode.path.isNotBlank()) {
            currentNode.path = newNode.path
        }

        if (newNode.children.isNotEmpty()) {
            for (newChildNode in newNode.children) {
                val currentChildNode = currentNode.children.find { it.name == newChildNode.name }

                if (currentChildNode != null) {
                    val updatedChildNode = updateNode(currentChildNode, newChildNode)
                    val currentNodeIndex = currentNode.children.indexOf(updatedChildNode)
                    currentNode.children[currentNodeIndex] = updatedChildNode
                } else {
                    currentNode.children.add(newChildNode)
                }
            }
        }

        return currentNode
    }

    companion object {
        const val SEPARATOR = "-"
    }
}
