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

import com.squareup.kotlinpoet.TypeSpec
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.Catalog
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.Node
import java.io.InputStream

internal interface InternalContract {

    interface GeneratorTask {
        fun generate(catalogSourceFolder: String, packageName: String, catalogNames: List<String>)
    }

    interface Generator {
        fun generate(catalog: Catalog): String

        interface VersionCatalogAccessorInterface {
            fun generate(catalog: Catalog): TypeSpec
        }

        interface VersionCatalogAccessorClass {
            fun generate(catalog: Catalog): TypeSpec
        }
    }

    interface VersionInterfaceGenerator {
        fun generate(packageName: String): String
    }

    interface Parser {
        fun parse(inputStream: InputStream): Catalog
    }

    interface Mapper {
        fun map(items: Map<String, String?>): List<Node>
    }
}
