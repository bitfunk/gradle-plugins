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

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class VersionCatalogConfigurationPluginFunctionalTest {

    @TempDir
    lateinit var tempDir: File

    lateinit var buildFile: File
    lateinit var settingsFile: File

    @BeforeEach
    fun setup() {
        buildFile = File(tempDir, "build.gradle.kts")
        settingsFile = File(tempDir, "settings.gradle.kts")
        settingsFile.writeText(SETTINGS_FILE_DEFAULT)
    }

    @Test
    fun `GIVEN Gradle version 7_1 WHEN run THEN fail`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)

        // WHEN
        val runner = setupRunner(tempDir)
            .withGradleVersion("7.1")
            .buildAndFail()

        // THEN
        Assertions.assertTrue(runner.output.contains("This plugin requires Gradle 7.2 or later"))
    }

    @Test
    fun `GIVEN inner module with plugin WHEN run THEN fail`() {
        // GIVEN
        settingsFile.writeText("include(\":inner\")")

        val innerDir = File(tempDir, "inner")
        innerDir.mkdir()
        val innerBuildFile = File(innerDir, "build.gradle.kts")
        innerBuildFile.writeText(BUILD_FILE_DEFAULT)

        // WHEN
        val runner = setupRunner(tempDir)
            .buildAndFail()

        // THEN
        Assertions.assertTrue(runner.output.contains("This plugin should be applied to root project only"))
    }

    @Test
    fun `GIVEN setup without java-gradle-plugin WHEN run THEN fail`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)

        // WHEN
        val runner = setupRunner(tempDir)
            .buildAndFail()

        // THEN
        Assertions.assertTrue(runner.output.contains("The VersionCatalogHelperPlugin requires the `java-gradle-plugin` to work."))
    }

    companion object {

        private fun setupRunner(projectDir: File): GradleRunner {
            return GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
        }

        val BUILD_FILE_DEFAULT = """
                plugins {
                    id("eu.bitfunk.gradle.version.catalog")
                }
            """.trimIndent()

        val SETTINGS_FILE_DEFAULT = """
            rootProject.name = "version-catalog-helper-test"
        """.trimIndent()
    }
}
