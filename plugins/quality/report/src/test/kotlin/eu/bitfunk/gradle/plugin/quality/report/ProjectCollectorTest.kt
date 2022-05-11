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

package eu.bitfunk.gradle.plugin.quality.report

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ProjectCollectorTest {

    @TempDir
    private lateinit var tempDir: File

    private lateinit var testSubject: ProjectCollector

    @BeforeEach
    fun setup() {
        testSubject = ProjectCollector()
    }

    @Test
    fun `implements contract`() {
        assertInstanceOf(
            ReportContract.Collector::class.java,
            testSubject
        )
    }

    @Test
    fun `GIVEN folder without nested projects WHEN collectProjects() THEN return empty list`() {
        // GIVEN
        val rootFolder = tempDir

        // WHEN
        val result = testSubject.collectProjects(rootFolder, "filter")

        // THEN
        assertEquals(
            emptyList<String>(),
            result
        )
    }

    @Test
    fun `GIVEN folder with filterPath WHEN collectProjects() THEN return root folder`() {
        // GIVEN
        val rootProjectSourceDir = File("$tempDir/src/main/kotlin/")
        rootProjectSourceDir.mkdirs()

        // WHEN
        val result = testSubject.collectProjects(tempDir, "src/main/kotlin")

        // THEN
        assertEquals(
            listOf("${rootProjectSourceDir.relativeTo(tempDir)}"),
            result
        )
    }

    @Test
    fun `GIVEN project with nested projects WHEN collectProjects() THEN return all projects`() {
        // GIVEN
        val rootProjectSourceDir = File(tempDir, "src/main/kotlin")
        rootProjectSourceDir.mkdirs()
        val subProject1SourceDir = File(tempDir, "project1/src/main/kotlin")
        subProject1SourceDir.mkdirs()
        val subProject2SourceDir = File(tempDir, "project2/src/main/kotlin")
        subProject2SourceDir.mkdirs()
        val nestedProjectSourceDir = File(tempDir, "project2/nestedProject/src/main/kotlin")
        nestedProjectSourceDir.mkdirs()

        // WHEN
        val result = testSubject.collectProjects(tempDir, "src/main/kotlin")

        // THEN
        assertEquals(
            listOf(
                "${rootProjectSourceDir.relativeTo(tempDir)}",
                "${subProject1SourceDir.relativeTo(tempDir)}",
                "${subProject2SourceDir.relativeTo(tempDir)}",
                "${nestedProjectSourceDir.relativeTo(tempDir)}"
            ),
            result
        )
    }
}
