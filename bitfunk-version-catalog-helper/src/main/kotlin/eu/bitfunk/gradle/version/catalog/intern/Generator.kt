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
import eu.bitfunk.gradle.version.catalog.VersionCatalogHelperContract.Dependency.Leaf
import eu.bitfunk.gradle.version.catalog.intern.model.Catalog
import org.gradle.api.Project
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
            .build()
    }

    private fun generateRootProperty(name: String, items: List<String>): PropertySpec {
        return PropertySpec.builder(name, Group::class)
            .initializer("%L", generateAnonymousImplementation(Group::class, items))
            .build()
    }

    private fun generateAnonymousImplementation(className: KClass<*>, items: List<String>): TypeSpec {
        return TypeSpec.anonymousClassBuilder()
            .addSuperinterface(className)
            .build()
    }

    companion object {
        const val PROPERTY_NAME_VERSIONS = "versions"
        const val PROPERTY_NAME_BUNDLES = "bundles"
        const val PROPERTY_NAME_PLUGINS = "plugins"

        const val CLASS_NAME_HELPER = "VersionCatalogHelper"

        const val HELPER_PROPERTY_NAME_PROJECT = "project"
        const val HELPER_PROPERTY_NAME_CATALOG_NAME = "catalogName"
    }
}
