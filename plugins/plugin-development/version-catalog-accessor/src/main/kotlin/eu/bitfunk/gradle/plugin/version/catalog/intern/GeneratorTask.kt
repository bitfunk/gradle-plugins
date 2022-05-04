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

import eu.bitfunk.gradle.plugin.version.catalog.intern.InternalContract.GeneratorTask
import org.gradle.configurationcache.extensions.capitalized
import java.io.File

internal class GeneratorTask(
    private val projectRootPath: File,
    private val projectBuildPath: File,
    private val mapper: InternalContract.Mapper = Mapper(),
    private val parser: InternalContract.Parser = Parser()
) : GeneratorTask {

    override fun generate(catalogSourceFolder: String, packageName: String, catalogNames: List<String>) {
        if (catalogNames.isEmpty()) return

        val catalogFiles: List<Pair<String, File>> = catalogNames.map {
            Pair(
                it,
                File("$projectRootPath/$catalogSourceFolder/$it$VERSION_CATALOG_EXTENSION")
            )
        }

        val generatedAccessors: List<Pair<String, String>> = catalogFiles
            .map { Pair(it.first, generateVersionCatalogAccessor(packageName, it.first, it.second)) }

        val outputDir = File("$projectBuildPath/$OUTPUT_PATH")
        if (!outputDir.exists()) outputDir.mkdirs()

        generatedAccessors.map { File("$outputDir/${it.first.capitalized()}$OUTPUT_FILE_NAME").writeText(it.second) }
    }

    private fun generateVersionCatalogAccessor(packageName: String, name: String, file: File): String {
        val generator = setupGenerator(packageName, name.capitalized())

        val catalog = parser.parse(file.inputStream())

        return generator.generate(catalog)
    }

    private fun setupGenerator(packageName: String, name: String): InternalContract.Generator = Generator(
        packageName = packageName,
        baseName = name,
        mapper
    )

    private companion object {
        private const val VERSION_CATALOG_EXTENSION = ".versions.toml"

        private const val OUTPUT_FILE_NAME = "VersionCatalogAccessor.kt"
        private const val OUTPUT_PATH = "generated/versionCatalogAccessor/src/main/kotlin"
    }
}
