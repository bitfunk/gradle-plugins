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
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.generated.BaseVersionCatalogAccessor
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.generated.VersionCatalogDependency
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.generated.VersionCatalogDependency.Group
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.generated.VersionCatalogDependency.GroupLeaf
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.generated.VersionCatalogDependency.Leaf
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.InternalContract.Generator
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.InternalContract.Mapper
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.Catalog
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Bundles
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Libraries
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Plugins
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Versions
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.Node
import org.gradle.api.Project
import kotlin.reflect.KClass

internal class Generator(
    private val packageName: String = "",
    private val baseName: String = "",
    private val mapper: Mapper
) : Generator {

    private fun generatePackage(): String {
        return "$packageName.generated"
    }

    override fun generate(catalog: Catalog): String {
        val accessorInterface = generateAccessorInterface(catalog)
        val accessorClass = generateAccessorClass(catalog)

        val file = FileSpec.builder(generatePackage(), generateClassBaseName() + CLASS_NAME_ACCESSOR)
            .indent("    ")
            .addImport(generatePackage(), "${accessorInterface.name}.${NAME_BUNDLES.capitalize()}")
            .addImport(generatePackage(), "${accessorInterface.name}.${NAME_LIBRARIES.capitalize()}")
            .addImport(generatePackage(), "${accessorInterface.name}.${NAME_PLUGINS.capitalize()}")
            .addImport(generatePackage(), "${accessorInterface.name}.${NAME_VERSIONS.capitalize()}")
            .addImport(
                VersionCatalogDependency::class.java.packageName,
                VersionCatalogDependency::class.java.simpleName
            )
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
        val versionNodes = mapper.map(catalog.versions.items)
        val libraryNodes = mapper.map(catalog.libraries.items)
        val bundleNodes = mapper.map(catalog.bundles.items)
        val pluginNodes = mapper.map(catalog.plugins.items)

        return TypeSpec.interfaceBuilder(generateClassBaseName() + INTERFACE_NAME_ACCESSOR)
            .addType(generateInterface(NAME_VERSIONS, versionNodes, null))
            .addType(generateInterface(NAME_LIBRARIES, libraryNodes, null))
            .addType(generateInterface(NAME_BUNDLES, bundleNodes, null))
            .addType(generateInterface(NAME_PLUGINS, pluginNodes, null))
            .build()
    }

    private fun generateInterface(name: String, nodes: List<Node>, kClass: KClass<*>?): TypeSpec {
        val interfaces = nodes
            .filter { (it.isGroup()) }
            .map {
                generateInterface(it.name, it.children, if (it.isLeaf()) GroupLeaf::class else Group::class)
            }

        return TypeSpec.interfaceBuilder(name.capitalize())
            .also { if (kClass != null) it.addSuperinterface(kClass) }
            .addProperties(generateInterfaceProperties(nodes))
            .addTypes(interfaces)
            .build()
    }

    private fun generateInterfaceProperties(
        nodes: List<Node>,
    ): Iterable<PropertySpec> {
        val properties = mutableListOf<PropertySpec>()

        for (node in nodes) {
            val property = if (node.isGroup() && node.isLeaf()) {
                generateInterfaceNodeProperty(
                    node,
                    ClassName("", node.name.capitalize()),
                )
            } else if (node.isLeaf()) {
                generateInterfaceNodeProperty(
                    node,
                    ClassName("", "VersionCatalogDependency.Leaf"),
                )
            } else {
                generateInterfaceNodeProperty(
                    node,
                    ClassName("", node.name.capitalize()),
                )
            }

            properties.add(property)
        }

        return properties
    }

    private fun generateInterfaceNodeProperty(
        node: Node,
        className: ClassName,
    ): PropertySpec {
        return PropertySpec.builder(node.name, className)
            .build()
    }

    private fun generateAccessorClass(catalog: Catalog): TypeSpec {
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
            .addSuperinterface(ClassName(generatePackage(), NAME_LIBRARIES.capitalize()))
            .addProperty(generateRootProperty(NAME_VERSIONS, catalog.versions))
            .addProperty(generateRootProperty(NAME_BUNDLES, catalog.bundles))
            .addProperty(generateRootProperty(NAME_PLUGINS, catalog.plugins))
            .addProperties(generateProperties(catalog.libraries::class, libraryNodes, NAME_LIBRARIES.capitalize()))
            .build()
    }

    private fun generateRootProperty(name: String, catalogEntry: CatalogEntry): PropertySpec {
        val className = ClassName(generatePackage(), name.capitalize())
        return PropertySpec.builder(name, className)
            .initializer("%L", generateRootImplementation(className, catalogEntry))
            .build()
    }

    private fun generateRootImplementation(className: ClassName, catalogEntry: CatalogEntry): TypeSpec {
        val nodes = mapper.map(catalogEntry.items)
        val properties = generateProperties(catalogEntry::class, nodes, className.simpleName)

        return TypeSpec.anonymousClassBuilder()
            .addSuperinterface(className)
            .addProperties(properties)
            .build()
    }

    private fun generateProperties(
        catalogType: KClass<*>,
        nodes: List<Node>,
        parentName: String
    ): Iterable<PropertySpec> {
        val properties = mutableListOf<PropertySpec>()

        for (node in nodes) {
            val property = if (node.isGroup() && node.isLeaf()) {
                val name = "$parentName.${node.name.capitalize()}"
                generateNodeProperty(
                    catalogType,
                    node,
                    ClassName(generatePackage(), name),
                    GroupLeaf::class,
                    name
                )
            } else if (node.isLeaf()) {
                generateNodeProperty(
                    catalogType,
                    node,
                    ClassName(generatePackage(), "VersionCatalogDependency.Leaf"),
                    Leaf::class,
                    parentName
                )
            } else {
                val name = "$parentName.${node.name.capitalize()}"
                generateNodeProperty(
                    catalogType,
                    node,
                    ClassName(generatePackage(), name),
                    Group::class,
                    name
                )
            }

            properties.add(property)
        }

        return properties
    }

    private fun generateNodeProperty(
        catalogType: KClass<*>,
        node: Node,
        className: ClassName,
        kClass: KClass<*>,
        parentName: String
    ): PropertySpec {
        return PropertySpec.builder(node.name, className)
            .addModifiers(OVERRIDE)
            .initializer("%L", generateNodeImplementation(catalogType, node, className, kClass, parentName))
            .build()
    }

    private fun generateNodeImplementation(
        catalogType: KClass<*>,
        node: Node,
        className: ClassName,
        kClass: KClass<*>,
        parentName: String
    ): TypeSpec {
        val nodeImplementation = TypeSpec.anonymousClassBuilder()
            .addSuperinterface(className)

        if (kClass == Leaf::class || kClass == GroupLeaf::class) {
            val function = generateFunction(catalogType, node.path)
            nodeImplementation.addFunction(function)
        }

        if (node.children.isNotEmpty()) {
            val properties = generateProperties(catalogType, node.children, parentName)
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
