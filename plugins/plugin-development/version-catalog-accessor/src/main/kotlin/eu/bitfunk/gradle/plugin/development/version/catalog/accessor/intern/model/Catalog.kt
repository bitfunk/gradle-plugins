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

package eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model

import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Bundles
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Libraries
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Plugins
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.model.CatalogEntry.Versions

internal data class Catalog(
    val versions: Versions,
    val libraries: Libraries,
    val bundles: Bundles,
    val plugins: Plugins
)

internal sealed class CatalogEntry(
    val items: Map<String, String?>
) {
    class Versions(items: Map<String, String>) : CatalogEntry(items)
    class Libraries(items: List<String>) : CatalogEntry(items.associateWith { null })
    class Bundles(items: List<String>) : CatalogEntry(items.associateWith { null })
    class Plugins(items: List<String>) : CatalogEntry(items.associateWith { null })
}
