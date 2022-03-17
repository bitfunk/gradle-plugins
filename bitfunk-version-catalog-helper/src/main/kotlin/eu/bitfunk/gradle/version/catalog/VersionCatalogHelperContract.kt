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

package eu.bitfunk.gradle.version.catalog

import eu.bitfunk.gradle.version.catalog.intern.model.Catalog
import eu.bitfunk.gradle.version.catalog.intern.model.Node
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import java.io.InputStream

interface VersionCatalogHelperContract {

    interface Plugin {
        fun addExtension(project: Project)
    }

    interface Extension {
        /**
         * Folder the version catalog files are stored in. Relative to project root.
         *
         * Default: gradle/
         */
        val catalogSourceFolder: Property<String>

        /**
         * Package name the generated VersionCatalogHelper is added to.
         *
         * Default: empty
         */
        val packageName: Property<String>

        /**
         * Names of the catalog file a VersionCatalogHelper should be generated for.
         *
         * Default: ["libs"]
         */
        val catalogNames: ListProperty<String>
    }

    interface Task {
        interface Generator {
            interface Intern {
                fun generate(catalogSourceFolder: String, packageName: String, catalogNames: List<String>)
            }
        }
    }

    interface Generator {
        fun generate(catalog: Catalog): String
    }

    interface Parser {
        fun parse(inputStream: InputStream): Catalog
    }

    interface Mapper {
        fun map(items: List<String>): List<Node>
    }

    interface ResourceLoader {
        fun loadAsString(filePath: String): String
    }
}
