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

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.InternalContract.Mapper
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.Catalog
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.Node

internal class VersionCatalogAccessorInterfaceGenerator(
    private val packageName: String = "",
    private val classBaseName: String = "",
    private val mapper: Mapper
) : InternalContract.Generator.VersionCatalogAccessorInterface {

    override fun generate(catalog: Catalog): TypeSpec {
        val versionNodes = mapper.map(catalog.versions.items)
        val libraryNodes = mapper.map(catalog.libraries.items)
        val bundleNodes = mapper.map(catalog.bundles.items)
        val pluginNodes = mapper.map(catalog.plugins.items)

        return TypeSpec.interfaceBuilder(classBaseName + INTERFACE_NAME)
            .addType(generateInterface(Generator.NAME_VERSIONS, versionNodes, null))
            .addType(generateInterface(Generator.NAME_LIBRARIES, libraryNodes, null))
            .addType(generateInterface(Generator.NAME_BUNDLES, bundleNodes, null))
            .addType(generateInterface(Generator.NAME_PLUGINS, pluginNodes, null))
            .build()
    }

    private fun generateInterface(name: String, nodes: List<Node>, kClass: ClassName?): TypeSpec {
        val interfaces = nodes
            .filter { (it.isGroup()) }
            .map {
                generateInterface(
                    it.name,
                    it.children,
                    if (it.isLeaf()) {
                        ClassName(packageName, "VersionCatalogDependency.GroupLeaf")
                    } else {
                        ClassName(packageName, "VersionCatalogDependency.Group")
                    }
                )
            }

        return TypeSpec.interfaceBuilder(name.capitalize())
            .also { if (kClass != null) it.addSuperinterface(kClass) }
            .addProperties(generateInterfaceProperties(nodes))
            .addTypes(interfaces)
            .build()
    }

    private fun generateInterfaceProperties(
        nodes: List<Node>
    ): Iterable<PropertySpec> {
        val properties = mutableListOf<PropertySpec>()

        for (node in nodes) {
            val property = if (!node.isGroup() && node.isLeaf()) {
                generateInterfaceNodeProperty(
                    node,
                    ClassName("", "VersionCatalogDependency.Leaf")
                )
            } else {
                generateInterfaceNodeProperty(
                    node,
                    ClassName("", node.name.capitalize())
                )
            }

            properties.add(property)
        }

        return properties
    }

    private fun generateInterfaceNodeProperty(
        node: Node,
        className: ClassName
    ): PropertySpec {
        return PropertySpec.builder(node.name, className)
            .build()
    }

    private companion object {
        const val INTERFACE_NAME = "VersionCatalogAccessorContract"
    }
}
