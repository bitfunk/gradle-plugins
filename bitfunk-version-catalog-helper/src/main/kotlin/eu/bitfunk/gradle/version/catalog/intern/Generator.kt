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

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import eu.bitfunk.gradle.version.catalog.VersionCatalogHelperContract
import eu.bitfunk.gradle.version.catalog.VersionCatalogHelperContract.Dependency.Group
import eu.bitfunk.gradle.version.catalog.VersionCatalogHelperContract.Dependency.GroupLeaf
import eu.bitfunk.gradle.version.catalog.VersionCatalogHelperContract.Dependency.Leaf
import eu.bitfunk.gradle.version.catalog.intern.model.Catalog
import eu.bitfunk.gradle.version.catalog.intern.model.CatalogEntry
import eu.bitfunk.gradle.version.catalog.intern.model.CatalogEntry.Bundles
import eu.bitfunk.gradle.version.catalog.intern.model.CatalogEntry.Libraries
import eu.bitfunk.gradle.version.catalog.intern.model.CatalogEntry.Plugins
import eu.bitfunk.gradle.version.catalog.intern.model.CatalogEntry.Versions
import eu.bitfunk.gradle.version.catalog.intern.model.Node
import org.gradle.api.Project
import java.lang.UnsupportedOperationException
import kotlin.reflect.KClass

class Generator(
    private val packageName: String = "",
    private val baseName: String = "",
    private val mapper: VersionCatalogHelperContract.Mapper
) : VersionCatalogHelperContract.Generator {

    override fun generate(catalog: Catalog): String {
        val helperClass = generateHelperClass(catalog)

        val file = FileSpec.builder(packageName, baseName + CLASS_NAME_HELPER)
            .indent("    ")
            .addType(helperClass)
            .build()

        return file.toString()
    }

    private fun generateHelperClass(catalog: Catalog): TypeSpec {
        val libraryNodes = mapper.map(catalog.libraries.items)

        return TypeSpec.classBuilder(baseName + CLASS_NAME_HELPER)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(HELPER_PROPERTY_NAME_PROJECT, Project::class)
                    .addParameter(HELPER_PROPERTY_NAME_CATALOG_NAME, String::class)
                    .build()
            )
            .superclass(BaseVersionCatalogHelper::class)
            .addSuperclassConstructorParameter("%N", HELPER_PROPERTY_NAME_PROJECT)
            .addSuperclassConstructorParameter("%N", HELPER_PROPERTY_NAME_CATALOG_NAME)
            .addProperty(generateRootProperty(PROPERTY_NAME_VERSIONS, catalog.versions))
            .addProperty(generateRootProperty(PROPERTY_NAME_BUNDLES, catalog.bundles))
            .addProperty(generateRootProperty(PROPERTY_NAME_PLUGINS, catalog.plugins))
            .addProperties(generateProperties(catalog.libraries::class, libraryNodes))
            .build()
    }

    private fun generateRootProperty(name: String, catalogEntry: CatalogEntry): PropertySpec {
        return PropertySpec.builder(name, Group::class)
            .initializer("%L", generateRootImplementation(Group::class, catalogEntry))
            .build()
    }

    private fun generateRootImplementation(className: KClass<*>, catalogEntry: CatalogEntry): TypeSpec {
        val nodes = mapper.map(catalogEntry.items)
        val properties = generateProperties(catalogEntry::class, nodes)

        return TypeSpec.anonymousClassBuilder()
            .addSuperinterface(className)
            .addProperties(properties)
            .build()
    }

    private fun generateProperties(catalogType: KClass<*>, nodes: List<Node>): Iterable<PropertySpec> {
        val properties = mutableListOf<PropertySpec>()

        for (node in nodes) {
            val property = if (node.isGroup() && node.isLeaf()) {
                generateNodeProperty(catalogType, node, GroupLeaf::class)
            } else if (node.isLeaf()) {
                generateNodeProperty(catalogType, node, Leaf::class)
            } else {
                generateNodeProperty(catalogType, node, Group::class)
            }

            properties.add(property)
        }

        return properties
    }

    private fun generateNodeProperty(catalogType: KClass<*>, node: Node, className: KClass<*>): PropertySpec {
        return PropertySpec.builder(node.name, className)
            .initializer("%L", generateNodeImplementation(catalogType, node, className))
            .build()
    }

    private fun generateNodeImplementation(catalogType: KClass<*>, node: Node, className: KClass<*>): TypeSpec {
        val nodeImplementation = TypeSpec.anonymousClassBuilder()
            .addSuperinterface(className)

        if (className == Leaf::class || className == GroupLeaf::class) {
            val function = generateFunction(catalogType, node.path)
            nodeImplementation.addFunction(function)
        }

        if (node.children.isNotEmpty()) {
            val properties = generateProperties(catalogType, node.children)
            nodeImplementation.addProperties(properties)
        }

        return nodeImplementation.build()
    }

    private fun generateFunction(catalogType: KClass<*>, path: String): FunSpec {
        val functionName: String = when (catalogType) {
            Versions::class -> "findVersion"
            Libraries::class -> "findLibrary"
            Bundles::class -> "findBundle"
            Plugins::class -> "findPlugin"
            else -> throw UnsupportedOperationException("$catalogType is not supported")
        }

        return FunSpec.builder(FUNCTION_NAME_GET)
            .addModifiers(OVERRIDE)
            .returns(String::class)
            .addStatement("return $functionName(\"$path\")")
            .build()
    }

    companion object {
        const val PROPERTY_NAME_VERSIONS = "versions"
        const val PROPERTY_NAME_BUNDLES = "bundles"
        const val PROPERTY_NAME_PLUGINS = "plugins"

        const val CLASS_NAME_HELPER = "VersionCatalogHelper"

        const val HELPER_PROPERTY_NAME_PROJECT = "project"
        const val HELPER_PROPERTY_NAME_CATALOG_NAME = "catalogName"

        const val FUNCTION_NAME_GET = "get"
    }
}