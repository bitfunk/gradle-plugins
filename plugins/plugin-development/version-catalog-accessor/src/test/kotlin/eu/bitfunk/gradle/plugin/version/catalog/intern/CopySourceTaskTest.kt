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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

public class CopySourceTaskTest {

    @TempDir
    private lateinit var outputDir: File

    private lateinit var copySourceTask: InternalContract.CopySourceTask

    @BeforeEach
    public fun setup() {
        copySourceTask = CopySourceTask()
    }

    @Test
    public fun `copySourceTask implements contract`() {
        assertInstanceOf(
            InternalContract.CopySourceTask::class.java,
            copySourceTask
        )
    }

    @Test
    public fun `GIVEN empty sources and empty output WHEN copy() THEN output exists and empty`() {
        // GIVEN
        val sources = listOf<String>()

        // WHEN
        copySourceTask.copy(sources, outputDir)

        // THEN
        assertTrue(outputDir.exists())
        assertTrue(outputDir.isDirEmpty())
    }

    @Test
    public fun `GIVEN empty source and output with non existing path WHEN copy() THEN output exists and empty`() {
        // GIVEN
        val sources = listOf<String>()
        val output = File("$outputDir/testFolder/one")

        // WHEN
        copySourceTask.copy(sources, output)

        // THEN
        assertTrue(output.exists())
        assertTrue(output.isDirEmpty())
    }

    @Test
    public fun `GIVEN sources and output WHEN copy() THEN output exists and contains sources`() {
        // GIVEN
        val sources = listOf("FileRoot.txt", "fixture/FileOne.txt", "fixture/FileTwo.txt")
        val output = File(outputDir, OUTPUT_PATH)

        // WHEN
        copySourceTask.copy(sources, output)

        // THEN
        assertTrue(output.exists())
        assertFalse(output.isDirEmpty())
        assertTrue(File(output, "FileRoot.txt").exists())
        assertEquals("root\n", File(output, "FileRoot.txt").readText())
        assertTrue(File(output, "FileOne.txt").exists())
        assertEquals("one\n", File(output, "FileOne.txt").readText())
        assertTrue(File(output, "FileTwo.txt").exists())
        assertEquals("two\n", File(output, "FileTwo.txt").readText())
    }

    private companion object {

        fun File.isDirEmpty(): Boolean = (listFiles()?.size == 0)

        private const val OUTPUT_PATH = "generated/versionCatalogAccessorAccessor/src/main/kotlin"
    }
}
