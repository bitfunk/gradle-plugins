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

package eu.bitfunk.gradle.plugin.tool.gitversion.internal.git

import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import java.text.ParseException
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

internal class GitLoaderTest {

    @TempDir
    private lateinit var tempDir: File

    @Test
    fun `implements contract`() {
        Assertions.assertInstanceOf(
            GitContract.Loader::class.java,
            GitLoader,
        )
    }

    @Test
    fun `GIVEN project without git WHEN load() THEN fail with exception`() {
        // GIVEN
        val projectDir = tempDir

        // WHEN
        val result = assertFails {
            GitLoader.load(projectDir)
        }

        // THEN
        assertEquals(
            IllegalArgumentException::class,
            result::class,
        )
        assertEquals(
            "Cannot find '.git' directory",
            result.message,
        )
    }

    @Test
    fun `GIVEN project with git WHEN load THEN return git`() {
        // GIVEN
        val projectDir = tempDir

        Git.init().setDirectory(projectDir).call()

        // WHEN
        GitLoader.load(projectDir)

        // THEN
        assertTrue(true)
    }

    @Test
    fun `GIVEN project is git submodule WHEN load THEN return git`() {
        // GIVEN
        val rootDir = tempDir
        val projectDir = Files.createDirectory(rootDir.toPath().resolve("submodule")).toFile()

        val submoduleSourceDir = Files.createDirectory(rootDir.toPath().resolve("submoduleSource")).toFile()
        val submoduleSourceGit = Git.init().setDirectory(submoduleSourceDir).call()

        val git = Git.init().setDirectory(rootDir).call()
        git.submoduleAdd()
            .setName("submodule")
            .setPath("submodule")
            .setURI(submoduleSourceGit.repository.directory.canonicalPath)
            .call()

        // WHEN
        GitLoader.load(projectDir)

        // THEN
        assertTrue(true)
    }

    @Test
    fun `GIVEN project is absolute git submodule WHEN load THEN return git`() {
        // GIVEN
        val rootDir = tempDir
        val projectDir = Files.createDirectory(rootDir.toPath().resolve("submodule")).toFile()

        val submoduleSourceDir = Files.createDirectory(rootDir.toPath().resolve("submoduleSource")).toFile()
        val submoduleSourceGit = Git.init().setDirectory(submoduleSourceDir).call()

        val git = Git.init().setDirectory(rootDir).call()
        git.submoduleAdd()
            .setName("submodule")
            .setPath("submodule")
            .setURI(submoduleSourceGit.repository.directory.canonicalPath)
            .call()

        val gitFile = File(projectDir, ".git")
        gitFile.writeText(gitFile.readText().replace("..", rootDir.toString()))

        // WHEN
        GitLoader.load(projectDir)

        // THEN
        assertTrue(true)
    }

    @Test
    fun `GIVEN project is no git submodule WHEN load THEN return git`() {
        // GIVEN
        val rootDir = tempDir
        val projectDir = Files.createDirectory(rootDir.toPath().resolve("submodule")).toFile()
        File(projectDir, ".git").createNewFile()

        Git.init().setDirectory(rootDir).call()

        // WHEN
        val result = assertFails {
            GitLoader.load(projectDir)
        }

        // THEN
        assertEquals(
            ParseException::class,
            result::class,
        )
        assertEquals(
            "$projectDir/.git",
            result.message,
        )
    }

    @Test
    fun `GIVEN project is wrong spacing git submodule WHEN load THEN return git`() {
        // GIVEN
        val rootDir = tempDir
        val projectDir = Files.createDirectory(rootDir.toPath().resolve("submodule")).toFile()
        val gitFile = File(projectDir, ".git")
        gitFile.createNewFile()
        gitFile.writeText("abc:def")

        Git.init().setDirectory(rootDir).call()

        // WHEN
        val result = assertFails {
            GitLoader.load(projectDir)
        }

        // THEN
        assertEquals(
            ParseException::class,
            result::class,
        )
        assertEquals(
            "abc",
            result.message,
        )
    }

    @Test
    fun `GIVEN project is wrong linebreak git submodule WHEN load THEN return git`() {
        // GIVEN
        val rootDir = tempDir
        val projectDir = Files.createDirectory(rootDir.toPath().resolve("submodule")).toFile()
        val gitFile = File(projectDir, ".git")
        gitFile.createNewFile()
        gitFile.writeText("gitdir:abc\ndef")

        Git.init().setDirectory(rootDir).call()

        // WHEN
        val result = assertFails {
            GitLoader.load(projectDir)
        }

        // THEN
        assertEquals(
            ParseException::class,
            result::class,
        )
        assertEquals(
            "abc\ndef",
            result.message,
        )
    }
}
