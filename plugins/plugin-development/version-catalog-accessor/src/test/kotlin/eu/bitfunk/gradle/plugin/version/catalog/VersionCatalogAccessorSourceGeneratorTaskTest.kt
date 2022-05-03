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

import org.gradle.api.internal.provider.MissingValueException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

public class VersionCatalogAccessorSourceGeneratorTaskTest {

    @TempDir
    private lateinit var projectDir: File

    @Test
    public fun `GIVEN project without properties WHEN generate() THEN fail`() {
        // GIVEN
        val project = ProjectBuilder.builder().build()
        val task = project.tasks.create("testTask", VersionCatalogAccessorSourceGeneratorTask::class.java)

        // WHEN/THEN
        Assertions.assertThrowsExactly(
            MissingValueException::class.java,
            { task.generate() },
            "Cannot query the value of task ':testTask' property 'catalogSourceFolder' because it has no value available."
        )
    }

    @Test
    public fun `GIVEN project with catalogSourceFolder WHEN generate() THEN fail`() {
        // GIVEN
        val project = ProjectBuilder.builder().build()
        val task = project.tasks.create("testTask", VersionCatalogAccessorSourceGeneratorTask::class.java)
        task.catalogSourceFolder.set("sourceFolder")

        // WHEN/THEN
        Assertions.assertThrowsExactly(
            MissingValueException::class.java,
            { task.generate() },
            "Cannot query the value of task ':testTask' property 'packageName' because it has no value available."
        )
    }

    @Test
    public fun `GIVEN project with catalogSourceFolder, packageName WHEN generate() THEN no output generated`() {
        // GIVEN
        val project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        val task = project.tasks.create("testTask", VersionCatalogAccessorSourceGeneratorTask::class.java)
        task.catalogSourceFolder.set("source")
        task.packageName.set("package.name")

        val outputFolder = File("${project.buildDir}/generated/versionCatalogAccessor/src/main/kotlin")

        // WHEN
        task.generate()

        // THEN
        Assertions.assertFalse(outputFolder.exists())
    }

    @Test
    public fun `GIVEN project configured WHEN generate() THEN generated present in output folder`() {
        // GIVEN
        val project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        val task = project.tasks.create("testTask", VersionCatalogAccessorSourceGeneratorTask::class.java)
        task.catalogSourceFolder.set("source")
        task.packageName.set("package.name")
        task.catalogNames.set(listOf("libs"))

        val outputFolder = File("${project.buildDir}/generated/versionCatalogAccessor/src/main/kotlin")

        File("$projectDir/source").mkdir()
        File(projectDir, "source/libs.versions.toml").writeText("")

        // WHEN
        task.generate()

        // THEN
        val libsAccessorFile = File("${outputFolder}/LibsVersionCatalogAccessor.kt")

        Assertions.assertTrue(outputFolder.exists())
        Assertions.assertTrue(libsAccessorFile.exists())
    }
}
