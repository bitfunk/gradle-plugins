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

package eu.bitfunk.gradle.plugin.development.version.catalog.accessor

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class VersionCatalogAccessorPluginFunctionalTest {

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
    fun `GIVEN Gradle version 7_1 WHEN run THEN fail`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)

        // WHEN
        val runner = setupRunner(tempDir)
            .withGradleVersion("7.1")
            .buildAndFail()

        // THEN
        assertTrue(runner.output.contains("This plugin requires Gradle 7.2 or later"))
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
        assertTrue(runner.output.contains("This plugin should be applied to root project only"))
    }

    @Test
    fun `GIVEN setup without java-gradle-plugin WHEN run THEN fail`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)

        // WHEN
        val runner = setupRunner(tempDir)
            .buildAndFail()

        // THEN
        assertTrue(runner.output.contains("The VersionCatalogAccessorPlugin requires the `java-gradle-plugin` to work."))
    }

    @Test
    fun `GIVEN default configuration WHEN generateVersionCatalogAccessor THEN catalog is present`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT_JAVA)

        File(tempDir, "gradle").mkdir()
        File("$tempDir/gradle", "libs.versions.toml").writeText(VERSION_CATALOG)

        // WHEN
        setupRunner(tempDir)
            .withArguments("generateVersionCatalogAccessorSource")
            .build()

        // THEN
        val outputFolder = File("$tempDir/build/generated/versionCatalogAccessor/src/main/kotlin")
        assertTrue(outputFolder.exists())
        assertEquals(1, outputFolder.listFiles()?.size)
        assertTrue(File(outputFolder, "LibsVersionCatalogAccessor.kt").exists())
    }

    @Test
    fun `GIVEN custom configuration WHEN generateVersionCatalogAccessor THEN catalogs are present`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT_JAVA_CONFIGURED)

        File(tempDir, "gradle").mkdir()
        File("$tempDir/gradle", "libs.versions.toml").writeText(VERSION_CATALOG)
        File("$tempDir/gradle", "deps.versions.toml").writeText(VERSION_CATALOG)

        // WHEN
        setupRunner(tempDir)
            .withArguments("generateVersionCatalogAccessorSource")
            .build()

        // THEN
        val outputFolder = File("$tempDir/build/generated/versionCatalogAccessor/src/main/kotlin")
        assertTrue(outputFolder.exists())
        assertEquals(2, outputFolder.listFiles()?.size)
        assertTrue(File(outputFolder, "DepsVersionCatalogAccessor.kt").exists())
        assertTrue(File(outputFolder, "LibsVersionCatalogAccessor.kt").exists())
    }

    @Test
    fun `GIVEN default configuration WHEN copyVersionCatalogAccessorSource THEN sources are present`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT_JAVA)

        // WHEN
        setupRunner(tempDir)
            .withArguments("copyVersionCatalogAccessorSource")
            .build()

        // THEN
        val outputFolder = File("$tempDir/build/generated/versionCatalogAccessor/src/main/kotlin")
        assertTrue(outputFolder.exists())
        assertEquals(2, outputFolder.listFiles()?.size)
        assertTrue(File(outputFolder, "BaseVersionCatalogAccessor.kt").exists())
        assertTrue(File(outputFolder, "VersionCatalogDependency.kt").exists())
    }

    @Test
    fun `GIVEN default configuration WHEN all run THEN all files are present`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT_JAVA)

        File(tempDir, "gradle").mkdir()
        File("$tempDir/gradle", "libs.versions.toml").writeText(VERSION_CATALOG)

        // WHEN
        setupRunner(tempDir)
            .withArguments("generateVersionCatalogAccessor")
            .build()

        // THEN
        val outputFolder = File("$tempDir/build/generated/versionCatalogAccessor/src/main/kotlin")
        assertTrue(outputFolder.exists())
        assertEquals(3, outputFolder.listFiles()?.size)
        assertTrue(File(outputFolder, "BaseVersionCatalogAccessor.kt").exists())
        assertTrue(File(outputFolder, "VersionCatalogDependency.kt").exists())
        assertTrue(File(outputFolder, "LibsVersionCatalogAccessor.kt").exists())
    }

    @Test
    fun `GIVEN custom configuration WHEN all run THEN all files are present`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT_JAVA_CONFIGURED)

        File(tempDir, "gradle").mkdir()
        File("$tempDir/gradle", "libs.versions.toml").writeText(VERSION_CATALOG)
        File("$tempDir/gradle", "deps.versions.toml").writeText(VERSION_CATALOG)

        // WHEN
        setupRunner(tempDir)
            .withArguments("generateVersionCatalogAccessor")
            .build()

        // THEN
        val outputFolder = File("$tempDir/build/generated/versionCatalogAccessor/src/main/kotlin")
        assertTrue(outputFolder.exists())
        assertEquals(4, outputFolder.listFiles()?.size)
        assertTrue(File(outputFolder, "BaseVersionCatalogAccessor.kt").exists())
        assertTrue(File(outputFolder, "VersionCatalogDependency.kt").exists())
        assertTrue(File(outputFolder, "LibsVersionCatalogAccessor.kt").exists())
        assertTrue(File(outputFolder, "DepsVersionCatalogAccessor.kt").exists())
    }

    private companion object {

        private fun setupRunner(projectDir: File): GradleRunner {
            return GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
        }

        val BUILD_FILE_DEFAULT = """
                plugins {
                    id("eu.bitfunk.gradle.plugin.development.version.catalog.accessor")
                }
        """.trimIndent()

        val BUILD_FILE_DEFAULT_JAVA = """
                plugins {
                    id("java-gradle-plugin")
                    id("eu.bitfunk.gradle.plugin.development.version.catalog.accessor")
                }
        """.trimIndent()

        val BUILD_FILE_DEFAULT_JAVA_CONFIGURED = """
                plugins {
                    id("java-gradle-plugin")
                    id("eu.bitfunk.gradle.plugin.development.version.catalog.accessor")
                }

                versionCatalogAccessor {
                    catalogNames.set(listOf("libs", "deps"))
                }
        """.trimIndent()

        val VERSION_CATALOG = """
            [versions]
            kotlin = "1.6.20"

            [libraries]
            kotlin = { module = "org.jetbrains.kotlin:kotlin", version.ref = "kotlin" }
        """.trimIndent()

        val SETTINGS_FILE_DEFAULT = """
            rootProject.name = "version-catalog-accessor-test"
        """.trimIndent()
    }
}
