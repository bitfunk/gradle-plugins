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

import com.fasterxml.jackson.dataformat.toml.TomlMapper
import eu.bitfunk.gradle.plugin.version.catalog.intern.InternalContract.Parser
import eu.bitfunk.gradle.plugin.version.catalog.intern.model.Catalog
import eu.bitfunk.gradle.plugin.version.catalog.intern.model.CatalogEntry.Bundles
import eu.bitfunk.gradle.plugin.version.catalog.intern.model.CatalogEntry.Libraries
import eu.bitfunk.gradle.plugin.version.catalog.intern.model.CatalogEntry.Plugins
import eu.bitfunk.gradle.plugin.version.catalog.intern.model.CatalogEntry.Versions
import java.io.InputStream

internal class Parser : Parser {

    override fun parse(inputStream: InputStream): Catalog {
        val mapper = TomlMapper()
        val catalog = inputStream.use {
            @Suppress("UNCHECKED_CAST")
            mapper.readValue(it, Map::class.java) as Map<String, Any>
        }

        return Catalog(
            versions = Versions(catalog.entry("versions").keys.toList()),
            libraries = Libraries(catalog.entry("libraries").keys.toList()),
            bundles = Bundles(catalog.entryList("bundles").keys.toList()),
            plugins = Plugins(catalog.entry("plugins").keys.toList()),
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any>.entryList(key: String) = get(key) as? Map<String, List<String>> ?: emptyMap()

    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any>.entry(key: String) = get(key) as? Map<String, Any> ?: emptyMap()
}
