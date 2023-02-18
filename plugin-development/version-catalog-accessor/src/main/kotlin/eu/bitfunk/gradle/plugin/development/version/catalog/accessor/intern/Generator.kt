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

import com.squareup.kotlinpoet.FileSpec
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.InternalContract.Generator
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.InternalContract.Mapper
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.Catalog
import org.gradle.api.artifacts.VersionCatalogsExtension

internal class Generator(
    private val packageName: String = "",
    private val baseName: String = "",
    private val mapper: Mapper
) : Generator {

    override fun generate(catalog: Catalog): String {
        val className = generateClassName()
        val classPackageName = generatePackageName()

        val interfaceGenerator = VersionCatalogAccessorInterfaceGenerator(classPackageName, className, mapper)
        val classGenerator = VersionCatalogAccessorClassGenerator(classPackageName, baseName, className, mapper)

        val accessorInterface = interfaceGenerator.generate(catalog)
        val accessorClass = classGenerator.generate(catalog)

        val file = FileSpec.builder(classPackageName, className + CLASS_NAME)
            .indent("    ")
            .addImport(classPackageName, "${accessorInterface.name}.${NAME_BUNDLES.titleCase()}")
            .addImport(classPackageName, "${accessorInterface.name}.${NAME_LIBRARIES.titleCase()}")
            .addImport(classPackageName, "${accessorInterface.name}.${NAME_PLUGINS.titleCase()}")
            .addImport(classPackageName, "${accessorInterface.name}.${NAME_VERSIONS.titleCase()}")
            .addImport(
                VersionCatalogsExtension::class.java.packageName,
                VersionCatalogsExtension::class.java.simpleName
            )
            .addType(accessorInterface)
            .addType(accessorClass)
            .build()

        return file.toString()
    }

    private fun generatePackageName(): String {
        return "$packageName.generated"
    }

    private fun generateClassName(): String {
        return baseName.split("-")
            .map { it.titleCase() }
            .joinToString(separator = "") { it }
    }

    companion object {
        const val NAME_VERSIONS = "versions"
        const val NAME_LIBRARIES = "libraries"
        const val NAME_BUNDLES = "bundles"
        const val NAME_PLUGINS = "plugins"

        const val ACCESSOR_PROPERTY_NAME_PROJECT = "project"

        const val CLASS_NAME = "VersionCatalogAccessor"
    }
}
