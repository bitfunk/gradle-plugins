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

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
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
import kotlin.test.assertSame
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
            GitVersionLoader(git, ""),
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
            actual = result::class,
        )
        assertEquals(
            expected = "Specified prefix `$prefix` does not match the allowed format regex `[/@]?([A-Za-z]+[/@-])+`.",
            actual = result.message,
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
    fun `GIVEN repository WHEN loadGitVersionInfo twice THEN cached version present`() {
        // GIVEN
        val loader = GitVersionLoader(git, "")

        // WHEN
        val result1 = loader.loadGitVersionInfo()
        val result2 = loader.loadGitVersionInfo()

        // THEN
        assertSame(
            expected = result1,
            actual = result2,
        )
    }

    @Test
    fun `GIVEN empty repository WHEN loadGitVersionInfo THEN unspecified version`() {
        // GIVEN

        // WHEN
        val result = GitVersionLoader(git, "").loadGitVersionInfo()

        // THEN
        assertEquals(
            expected = GitVersionInfo(
                version = "unspecified",
                versionCode = -1,
                branchName = "unspecified",
                gitHashFull = "unspecified",
                gitHash = "unspecified",
                lastTag = "unspecified",
                isCleanTag = true,
                commitDistance = 0,
            ),
            actual = result,
        )
    }

    @Test
    fun `GIVEN no tag WHEN loadGitVersionInfo THEN version is hash`() {
        // GIVEN
        git.add().addFilepattern(".").call()
        val lastCommit = git.commit().setMessage("initial commit").call()

        // WHEN
        val result = GitVersionLoader(git, "").loadGitVersionInfo()

        // THEN
        assertEquals(
            expected = GitVersionInfo(
                version = lastCommit.name.take(Constants.OBJECT_ID_ABBREV_STRING_LENGTH),
                versionCode = -1,
                branchName = "master",
                gitHashFull = lastCommit.name,
                gitHash = lastCommit.name.take(Constants.OBJECT_ID_ABBREV_STRING_LENGTH),
                lastTag = "unspecified",
                isCleanTag = true,
                commitDistance = 0,
            ),
            actual = result,
        )
    }

    @Test
    fun `GIVEN with tag and no additional commit WHEN describe THEN describe is tag`() {
        // GIVEN
        val git = Git.init().setDirectory(tempDir).call()
        git.add().addFilepattern(".").call()
        val lastCommit = git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()

        // WHEN
        val result = GitVersionLoader(git, "").loadGitVersionInfo()

        // THEN
        assertEquals(
            expected = GitVersionInfo(
                version = "1.0.0",
                versionCode = 10000,
                branchName = "master",
                gitHashFull = lastCommit.name,
                gitHash = lastCommit.name.take(Constants.OBJECT_ID_ABBREV_STRING_LENGTH),
                lastTag = "1.0.0",
                isCleanTag = true,
                commitDistance = 0,
            ),
            actual = result,
        )
    }

    @Test
    fun `GIVEN with v tag and no additional commit WHEN describe THEN describe is tag`() {
        // GIVEN
        val git = Git.init().setDirectory(tempDir).call()
        git.add().addFilepattern(".").call()
        val lastCommit = git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("v1.0.0").setName("v1.0.0").call()

        // WHEN
        val result = GitVersionLoader(git, "").loadGitVersionInfo()

        // THEN
        assertEquals(
            expected = GitVersionInfo(
                version = "v1.0.0",
                versionCode = 10000,
                branchName = "master",
                gitHashFull = lastCommit.name,
                gitHash = lastCommit.name.take(Constants.OBJECT_ID_ABBREV_STRING_LENGTH),
                lastTag = "v1.0.0",
                isCleanTag = true,
                commitDistance = 0,
            ),
            actual = result,
        )
    }

    @Test
    fun `GIVEN with tag and additional commit WHEN describe THEN describe is tag and count up by one`() {
        // GIVEN
        val git = Git.init().setDirectory(tempDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()
        val lastCommit = git.commit().setMessage("commit 2").call()

        // WHEN
        val result = GitVersionLoader(git, "").loadGitVersionInfo()

        // THEN
        val lastCommitShort = lastCommit.name.take(Constants.OBJECT_ID_ABBREV_STRING_LENGTH)
        assertEquals(
            expected = GitVersionInfo(
                version = "1.0.0-1-g$lastCommitShort",
                versionCode = 10000,
                branchName = "master",
                gitHashFull = lastCommit.name,
                gitHash = lastCommitShort,
                lastTag = "1.0.0",
                isCleanTag = false,
                commitDistance = 1,
            ),
            actual = result,
        )
    }

    @Test
    fun `GIVEN tag set on deep commit WHEN describe() THEN return description`() {
        // GIVEN
        val git = Git.init().setDirectory(tempDir).call()
        git.add().addFilepattern(".").call()
        var lastCommit = git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()

        val depth = 100
        for (i in 1..depth) {
            git.add().addFilepattern(".").call()
            lastCommit = git.commit().setMessage("commit-$i").call()
        }

        // WHEN
        val result = GitVersionLoader(git, "").loadGitVersionInfo()

        // THEN
        val lastCommitShort = lastCommit.name.take(Constants.OBJECT_ID_ABBREV_STRING_LENGTH)
        assertEquals(
            expected = GitVersionInfo(
                version = "1.0.0-$depth-g$lastCommitShort",
                versionCode = 10000,
                branchName = "master",
                gitHashFull = lastCommit.name,
                gitHash = lastCommitShort,
                lastTag = "1.0.0",
                isCleanTag = false,
                commitDistance = depth,
            ),
            actual = result,
        )
    }

    @Test
    fun `GIVEN multiple tags with some having prefix WHEN describe() THEN prefixed tag is shown without prefix`() {
        // GIVEN
        val prefix = "my-prefix@"
        val git = Git.init().setDirectory(tempDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("${prefix}1.0.0").setName("${prefix}1.0.0").call()
        val lastCommit = git.commit().setMessage("commit 2").call()
        git.tag().setAnnotated(true).setMessage("1.1.0").setName("1.1.0").call()

        // WHEN
        val result = GitVersionLoader(git, prefix).loadGitVersionInfo()

        // THEN
        val lastCommitShort = lastCommit.name.take(Constants.OBJECT_ID_ABBREV_STRING_LENGTH)
        assertEquals(
            expected = GitVersionInfo(
                version = "1.0.0-1-g$lastCommitShort",
                versionCode = 10000,
                branchName = "master",
                gitHashFull = lastCommit.name,
                gitHash = lastCommitShort,
                lastTag = "1.0.0",
                isCleanTag = false,
                commitDistance = 1,
            ),
            actual = result,
        )
    }

    @Test
    fun `GIVEN symlinks WHEN loadGitVersionInfo THEN clean version`() {
        // GIVEN
        val fileToLinkTo = File(tempDir, "fileToLinkTo")
        fileToLinkTo.writeText("content")
        Files.createSymbolicLink(
            tempDir.toPath().resolve("fileLink"),
            fileToLinkTo.toPath(),
        )

        val folderToLinkTo = File(tempDir, "folderToLinkTo")
        folderToLinkTo.mkdir()
        File(folderToLinkTo, "dummyFile").writeText("content")
        Files.createSymbolicLink(
            tempDir.toPath().resolve("folderLink"),
            folderToLinkTo.toPath(),
        )

        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("message").setName("1.0.0").call()

        // WHEN
        val result = GitVersionLoader(git, "").loadGitVersionInfo()

        // THEN
        assertEquals(
            "1.0.0",
            result.version,
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
            result.version,
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
            result.version,
        )
    }

    private companion object {
        val PERSON_IDENT = PersonIdent(
            "name",
            "email@example.com",
            Date(1234L),
            TimeZone.getTimeZone("UTC"),
        )
    }
}
