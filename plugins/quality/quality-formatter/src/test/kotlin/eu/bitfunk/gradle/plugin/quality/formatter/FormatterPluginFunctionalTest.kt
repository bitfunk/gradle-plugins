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

package eu.bitfunk.gradle.plugin.quality.formatter

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class FormatterPluginFunctionalTest {

    @TempDir
    private lateinit var tempDir: File

    private lateinit var buildFile: File
    private lateinit var settingsFile: File

    @BeforeEach
    fun setup() {
        buildFile = File(tempDir, "build.gradle.kts")
        settingsFile = File(tempDir, "settings.gradle.kts")
        settingsFile.writeText(SETTINGS_FILE_DEFAULT)
    }

    @Test
    fun `GIVEN correct kotlin file WHEN spotlessCheck THEN success`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)

        val sourceDir = File(tempDir, "src/main/kotlin/com/example/")
        sourceDir.mkdirs()
        val sourceFile = File(sourceDir, "Example.kt")
        sourceFile.writeText(KOTLIN_FILE_CORRECT)

        // WHEN/THEN
        setupRunner(tempDir)
            .withArguments("spotlessCheck")
            .build()
    }

    @Test
    fun `GIVEN incorrect kotlin file WHEN spotlessCheck THEN failure`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)

        val sourceDir = File(tempDir, "src/main/kotlin/com/example/")
        sourceDir.mkdirs()
        val sourceFile = File(sourceDir, "Example.kt")
        sourceFile.writeText(KOTLIN_FILE_INCORRECT)

        // WHEN/THEN
        setupRunner(tempDir)
            .withArguments("spotlessCheck")
            .buildAndFail()
    }

    @Test
    fun `GIVEN incorrect kotlin files in special folder WHEN spotlessCheck THEN success`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)

        val sourceDirResources = File(tempDir, "src/main/resources/com/example/")
        sourceDirResources.mkdirs()
        val sourceFileResources = File(sourceDirResources, "ExampleResource.kt")
        sourceFileResources.writeText(KOTLIN_FILE_INCORRECT)

        val sourceDirBuild = File(tempDir, "build/com/example/")
        sourceDirBuild.mkdirs()
        val sourceFileBuild = File(sourceDirBuild, "ExampleBuild.kt")
        sourceFileBuild.writeText(KOTLIN_FILE_INCORRECT)

        // WHEN/THEN
        setupRunner(tempDir)
            .withArguments("spotlessCheck")
            .build()
    }

    @Test
    fun `GIVEN correct kotlin gradle file WHEN spotlessCheck THEN success `() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)

        val sourceDir = File(tempDir, "module")
        sourceDir.mkdirs()
        val sourceFile = File(sourceDir, "example.gradle.kts")
        sourceFile.writeText(KOTLIN_FILE_CORRECT)

        // WHEN/THEN
        setupRunner(tempDir)
            .withArguments("spotlessCheck")
            .build()
    }

    @Test
    fun `GIVEN incorrect kotlin gradle file WHEN spotlessCheck THEN failure `() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)

        val sourceDir = File(tempDir, "module")
        sourceDir.mkdirs()
        val sourceFile = File(sourceDir, "example.gradle.kts")
        sourceFile.writeText(KOTLIN_FILE_INCORRECT)

        // WHEN/THEN
        setupRunner(tempDir)
            .withArguments("spotlessCheck")
            .buildAndFail()
    }

    private companion object {
        private fun setupRunner(projectDir: File): GradleRunner {
            return GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
        }

        val BUILD_FILE_DEFAULT = """
                plugins {
                    id("eu.bitfunk.gradle.plugin.quality.formatter")
                }

        """.trimIndent()

        val SETTINGS_FILE_DEFAULT = """
            rootProject.name = "quality-format-test"

        """.trimIndent()

        val KOTLIN_FILE_CORRECT = """
            package com.example

            class Example {
                fun calculate(): Int {
                    return 2 + 2
                }
            }

        """.trimIndent()

        val KOTLIN_FILE_INCORRECT = """
            package com.example

            class Example{

                fun calculate( ):
                Int {
                    return 2 + 2
                }
            }

        """.trimIndent()
    }
}
