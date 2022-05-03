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

package eu.bitfunk.gradle.plugin.version.catalog

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

public class VersionCatalogAccessorSourceCopyTaskTest {

    @Test
    public fun `GIVEN sources WHEN copy() THEN sources in project buildDir`() {
        // GIVEN
        val project = ProjectBuilder.builder().build()
        val task = project.tasks.create("testTask", VersionCatalogAccessorSourceCopyTask::class.java)
        val output = File("${project.buildDir}/$OUTPUT_PATH")

        // WHEN
        task.copySource()

        // THEN
        assertTrue(output.exists())
    }

    private companion object {
        private const val OUTPUT_PATH = "generated/versionCatalogAccessor/src/main/kotlin"
    }
}
