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

package eu.bitfunk.gradle.plugin.tool.gitversion

import eu.bitfunk.gradle.plugin.tool.gitversion.internal.git.GitContract
import eu.bitfunk.gradle.plugin.tool.gitversion.internal.git.GitLoader
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.PersonIdent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import java.util.Date
import java.util.TimeZone
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

internal class GitVersionLoaderTest {

    @TempDir
    private lateinit var tempDir: File

    private lateinit var git: Git

    @BeforeEach
    fun setup() {
        git = Git.init().setDirectory(tempDir).call()
    }

    @Test
    fun `implements contract`() {
        Assertions.assertInstanceOf(
            GitVersionContract.Loader::class.java,
            GitVersionLoader(git, "")
        )
    }

    @Test
    fun `GIVEN prefix not matching pattern WHEN created THEN fail`() {
        // GIVEN
        val prefix = "abc"

        // WHEN
        val result = assertFails {
            GitVersionLoader(git, prefix)
        }

        // THEN
        assertEquals(
            expected = AssertionError::class,
            actual = result::class
        )
        assertEquals(
            expected = "Specified prefix `$prefix` does not match the allowed format regex `[/@]?([A-Za-z]+[/@-])+`.",
            actual = result.message
        )
    }

    @Test
    fun `GIVEN prefix matching pattern WHEN created THEN new instance`() {
        // GIVEN
        val prefix = "abc@"

        // WHEN
        GitVersionLoader(git, prefix)

        // THEN
        assertTrue(actual = true, message = "instance available")
    }

    @Test
    fun `GIVEN empty prefix WHEN created THEN new instance`() {
        // GIVEN
        val prefix = ""

        // WHEN
        GitVersionLoader(git, prefix)

        // THEN
        assertTrue(actual = true, message = "instance available")
    }

    @Test
    fun `GIVEN symlinks WHEN loadGitVersionInfo THEN clean version`() {
        // GIVEN
        val fileToLinkTo = File(tempDir, "fileToLinkTo")
        fileToLinkTo.writeText("content")
        Files.createSymbolicLink(
            tempDir.toPath().resolve("fileLink"),
            fileToLinkTo.toPath()
        )

        val folderToLinkTo = File(tempDir, "folderToLinkTo")
        folderToLinkTo.mkdir()
        File(folderToLinkTo, "dummyFile").writeText("content")
        Files.createSymbolicLink(
            tempDir.toPath().resolve("folderLink"),
            folderToLinkTo.toPath()
        )

        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("message").setName("1.0.0").call()

        // WHEN
        val result = GitVersionLoader(git, "").loadGitVersionInfo()

        // THEN
        assertEquals(
            "1.0.0",
            result.version
        )
    }

    @Test
    fun `GIVEN no tags WHEN loadGitVersionInfo THEN version is hash`() {
        // GIVEN
        git.add().addFilepattern(".").call()
        git.commit()
            .setAuthor(PERSON_IDENT)
            .setCommitter(PERSON_IDENT)
            .setMessage("initial commit")
            .call()

        // WHEN
        val result = GitVersionLoader(git, "").loadGitVersionInfo()

        // THEN
        assertEquals(
            "44c4231",
            result.version
        )
    }

    @Test
    fun `GIVEN no tags and dirty content WHEN loadGitVersionInfo THEN version is hash with dirty flag`() {
        // GIVEN
        git.add().addFilepattern(".").call()
        git.commit()
            .setAuthor(PERSON_IDENT)
            .setCommitter(PERSON_IDENT)
            .setMessage("initial commit")
            .call()

        File(tempDir, "someFile").writeText("content")

        // WHEN
        val result = GitVersionLoader(git, "").loadGitVersionInfo()

        // THEN
        assertEquals(
            "44c4231.dirty",
            result.version
        )
    }

    private companion object {
        val PERSON_IDENT = PersonIdent(
            "name", "email@example.com", Date(1234L), TimeZone.getTimeZone("UTC")
        )
    }
}
