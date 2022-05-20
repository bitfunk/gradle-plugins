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
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.InternalContract.Mapper
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.Catalog
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Bundles
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Libraries
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Plugins
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Versions
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.Node
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import kotlin.reflect.KClass

internal class VersionCatalogAccessorClassGenerator(
    private val packageName: String = "",
    private val baseName: String = "",
    private val classBaseName: String = "",
    private val mapper: Mapper
) : InternalContract.Generator.VersionCatalogAccessorClass {

    override fun generate(catalog: Catalog): TypeSpec {
        val libraryNodes = mapper.map(catalog.libraries.items)

        return TypeSpec.classBuilder(classBaseName + Generator.CLASS_NAME)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(Generator.ACCESSOR_PROPERTY_NAME_PROJECT, Project::class)
                    .build()
            ).addProperty(
                PropertySpec.builder(Generator.ACCESSOR_PROPERTY_NAME_PROJECT, Project::class)
                    .initializer(Generator.ACCESSOR_PROPERTY_NAME_PROJECT)
                    .addModifiers(PRIVATE)
                    .build()
            )
            .addSuperinterface(ClassName(packageName, Generator.NAME_LIBRARIES.capitalize()))
            .addProperty(
                PropertySpec.builder("versionCatalog", VersionCatalog::class.java)
                    .getter(
                        FunSpec.getterBuilder()
                            .addStatement(
                                "return %L",
                                "project.extensions.getByType(VersionCatalogsExtension::class.java)\n" +
                                    "    .named(\"${baseName.decapitalize()}\")"
                            )
                            .build()
                    )
                    .addModifiers(PRIVATE)
                    .build()
            )
            .addProperty(generateRootProperty(Generator.NAME_VERSIONS, catalog.versions))
            .addProperty(generateRootProperty(Generator.NAME_BUNDLES, catalog.bundles))
            .addProperty(generateRootProperty(Generator.NAME_PLUGINS, catalog.plugins))
            .addProperties(
                generateProperties(
                    catalog.libraries::class,
                    libraryNodes,
                    Generator.NAME_LIBRARIES.capitalize()
                )
            )
            .addFunction(generateCatalogFindFunction("findVersion"))
            .addFunction(generateCatalogFindFunction("findLibrary"))
            .addFunction(generateCatalogFindFunction("findBundle"))
            .addFunction(generateCatalogFindFunction("findPlugin"))
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
                    ClassName(packageName, name),
                    ClassName(packageName, "GroupLeaf"),
                    name
                )
            } else if (node.isLeaf()) {
                generateNodeProperty(
                    catalogType,
                    node,
                    ClassName(packageName, "VersionCatalogDependency.Leaf"),
                    ClassName(packageName, "Leaf"),
                    parentName
                )
            } else {
                val name = "$parentName.${node.name.capitalize()}"
                generateNodeProperty(
                    catalogType,
                    node,
                    ClassName(packageName, name),
                    ClassName(packageName, "Group"),
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
        kClass: ClassName,
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
        kClass: ClassName,
        parentName: String
    ): TypeSpec {
        val nodeImplementation = TypeSpec.anonymousClassBuilder()
            .addSuperinterface(className)

        if (kClass.simpleName == "Leaf" || kClass.simpleName == "GroupLeaf") {
            val getFunction = generateGetFunction(catalogType, node.path)
            nodeImplementation.addFunction(getFunction)
            val getStaticFunction = generateGetStaticFunction(catalogType, node.value)
            nodeImplementation.addFunction(getStaticFunction)
        }

        if (node.children.isNotEmpty()) {
            val properties = generateProperties(catalogType, node.children, parentName)
            nodeImplementation.addProperties(properties)
        }

        return nodeImplementation.build()
    }

    private fun generateGetFunction(catalogType: KClass<*>, path: String): FunSpec {
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

    private fun generateGetStaticFunction(catalogType: KClass<*>, value: String?): FunSpec {
        val satement: String = when (catalogType) {
            Versions::class -> "return \"$value\""
            Libraries::class -> "throw UnsupportedOperationException(\n    \"not yet implemented\"\n)"
            Bundles::class -> "throw UnsupportedOperationException(\n    \"not yet implemented\"\n)"
            Plugins::class -> "throw UnsupportedOperationException(\n    \"not yet implemented\"\n)"
            else -> throw UnsupportedOperationException("$catalogType is not supported")
        }

        return FunSpec.builder(FUNCTION_NAME_GET_STATIC)
            .addModifiers(OVERRIDE)
            .returns(String::class)
            .addStatement(satement)
            .build()
    }

    private fun generateCatalogFindFunction(name: String): FunSpec {
        return FunSpec.builder(name)
            .addModifiers(PRIVATE)
            .addParameter("name", String::class)
            .returns(String::class)
            .addStatement("try {")
            .also {
                if ("findVersion" == name)
                    it.addStatement("    return versionCatalog.$name(name).get().requiredVersion")
                else
                    it.addStatement("    return versionCatalog.$name(name).get().get().toString()")
            }
            .addStatement("} catch (error: Throwable) {")
            .addStatement("    throw NoSuchElementException(")
            .addStatement("        \"Can't $name accessor in $baseName.versions.toml: \$name\"")
            .addStatement("    )")
            .addStatement("}")
            .build()
    }

    private companion object {
        const val FUNCTION_NAME_GET = "get"
        const val FUNCTION_NAME_GET_STATIC = "getStatic"
    }
}
