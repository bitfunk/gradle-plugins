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
import com.squareup.kotlinpoet.TypeSpec

internal class VersionInterfaceGenerator : InternalContract.VersionInterfaceGenerator {

    override fun generate(packageName: String): String {
        val versionInterface = generateVersionInterface(packageName)

        val file = FileSpec.builder(packageName, INTERFACE_NAME)
            .indent("    ")
            .addType(versionInterface)
            .build()

        return file.toString()
    }

    private fun generateVersionInterface(packageName: String): TypeSpec {
        return TypeSpec.interfaceBuilder(INTERFACE_NAME)
            .addType(
                TypeSpec.interfaceBuilder("Group")
                    .addSuperinterface(ClassName(packageName, INTERFACE_NAME))
                    .build()
            )
            .addType(
                TypeSpec.interfaceBuilder("GroupLeaf")
                    .addSuperinterface(ClassName(packageName, INTERFACE_NAME))
                    .addFunction(
                        FunSpec.builder("get")
                            .returns(String::class)
                            .build()
                    )
                    .build()
            )
            .addType(
                TypeSpec.interfaceBuilder("Leaf")
                    .addSuperinterface(ClassName(packageName, INTERFACE_NAME))
                    .addFunction(
                        FunSpec.builder("get")
                            .returns(String::class)
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private companion object {
        const val INTERFACE_NAME = "VersionCatalogDependency"
    }
}
