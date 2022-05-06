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

package eu.bitfunk.gradle.plugin.version.catalog.intern

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import eu.bitfunk.gradle.plugin.version.catalog.accessor.BaseVersionCatalogAccessor
import eu.bitfunk.gradle.plugin.version.catalog.accessor.VersionCatalogDependency.Group
import eu.bitfunk.gradle.plugin.version.catalog.accessor.VersionCatalogDependency.GroupLeaf
import eu.bitfunk.gradle.plugin.version.catalog.accessor.VersionCatalogDependency.Leaf
import eu.bitfunk.gradle.plugin.version.catalog.intern.InternalContract.Generator
import eu.bitfunk.gradle.plugin.version.catalog.intern.InternalContract.Mapper
import eu.bitfunk.gradle.plugin.version.catalog.intern.model.Catalog
import eu.bitfunk.gradle.plugin.version.catalog.intern.model.CatalogEntry
import eu.bitfunk.gradle.plugin.version.catalog.intern.model.CatalogEntry.Bundles
import eu.bitfunk.gradle.plugin.version.catalog.intern.model.CatalogEntry.Libraries
import eu.bitfunk.gradle.plugin.version.catalog.intern.model.CatalogEntry.Plugins
import eu.bitfunk.gradle.plugin.version.catalog.intern.model.CatalogEntry.Versions
import eu.bitfunk.gradle.plugin.version.catalog.intern.model.Node
import org.gradle.api.Project
import kotlin.reflect.KClass

internal class Generator(
    private val packageName: String = "",
    private val baseName: String = "",
    private val mapper: Mapper
) : Generator {

    override fun generate(catalog: Catalog): String {
        val accessorInterface = generateAccessorInterface(catalog)
        val accessorClass = generateAccessorClass(catalog, accessorInterface)

        val file = FileSpec.builder(packageName, generateClassBaseName() + CLASS_NAME_ACCESSOR)
            .indent("    ")
            .addImport(packageName, "${accessorInterface.name}.${NAME_BUNDLES.capitalize()}")
            .addImport(packageName, "${accessorInterface.name}.${NAME_LIBRARIES.capitalize()}")
            .addImport(packageName, "${accessorInterface.name}.${NAME_PLUGINS.capitalize()}")
            .addImport(packageName, "${accessorInterface.name}.${NAME_VERSIONS.capitalize()}")
            .addType(accessorInterface)
            .addType(accessorClass)
            .build()

        return file.toString()
    }

    private fun generateClassBaseName(): String {
        return baseName.split("-")
            .map { it.capitalize() }
            .joinToString(separator = "") { it }
    }

    private fun generateAccessorInterface(catalog: Catalog): TypeSpec {
        return TypeSpec.interfaceBuilder(generateClassBaseName() + INTERFACE_NAME_ACCESSOR)
            .addType(generateInterface(NAME_VERSIONS))
            .addType(generateInterface(NAME_LIBRARIES))
            .addType(generateInterface(NAME_BUNDLES))
            .addType(generateInterface(NAME_PLUGINS))
            .build()
    }

    private fun generateInterface(name: String): TypeSpec {
        return TypeSpec.interfaceBuilder(name.capitalize())
            .build()
    }

    private fun generateAccessorClass(catalog: Catalog, accessorInterface: TypeSpec): TypeSpec {
        val libraryNodes = mapper.map(catalog.libraries.items)

        return TypeSpec.classBuilder(generateClassBaseName() + CLASS_NAME_ACCESSOR)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(ACCESSOR_PROPERTY_NAME_PROJECT, Project::class)
                    .build()
            )
            .superclass(BaseVersionCatalogAccessor::class)
            .addSuperclassConstructorParameter("%N", ACCESSOR_PROPERTY_NAME_PROJECT)
            .addSuperclassConstructorParameter("%S", baseName)
            .addSuperinterface(ClassName(packageName, NAME_LIBRARIES.capitalize()))
            .addProperty(generateRootProperty(NAME_VERSIONS, catalog.versions))
            .addProperty(generateRootProperty(NAME_BUNDLES, catalog.bundles))
            .addProperty(generateRootProperty(NAME_PLUGINS, catalog.plugins))
            .addProperties(generateProperties(catalog.libraries::class, libraryNodes))
            .build()
    }

    private fun generateRootProperty(name: String, catalogEntry: CatalogEntry): PropertySpec {
        val className = ClassName(packageName, name.capitalize())
        return PropertySpec.builder(name, className)
            .initializer("%L", generateRootImplementation(className, catalogEntry))
            .build()
    }

    private fun generateRootImplementation(className: ClassName, catalogEntry: CatalogEntry): TypeSpec {
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
        const val NAME_VERSIONS = "versions"
        const val NAME_LIBRARIES = "libraries"
        const val NAME_BUNDLES = "bundles"
        const val NAME_PLUGINS = "plugins"

        const val INTERFACE_NAME_ACCESSOR = "VersionCatalogAccessorContract"
        const val CLASS_NAME_ACCESSOR = "VersionCatalogAccessor"

        const val ACCESSOR_PROPERTY_NAME_PROJECT = "project"

        const val FUNCTION_NAME_GET = "get"
    }
}
