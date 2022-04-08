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

import eu.bitfunk.gradle.version.catalog.intern.CopySourceTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

public abstract class VersionCatalogAccessorSourceCopyTask : DefaultTask(), VersionCatalogAccessorContract.Task.CopySource {

    private val copySourceTask = CopySourceTask()

    @TaskAction
    override fun copySource() {
        val outputDir = File("${project.buildDir}/$OUTPUT_PATH")
        copySourceTask.copy(SOURCES, outputDir)
    }

    private companion object {
        private val SOURCES = listOf("sources/BaseVersionCatalogAccessor.kt", "sources/VersionCatalogDependency.kt")

        private const val OUTPUT_PATH = "generated/versionCatalogAccessor/src/main/kotlin"
    }
}
