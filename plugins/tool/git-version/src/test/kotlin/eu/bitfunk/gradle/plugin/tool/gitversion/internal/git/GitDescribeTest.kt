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

import io.mockk.mockk
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.MergeCommand
import org.eclipse.jgit.api.errors.RefNotFoundException
import org.eclipse.jgit.lib.Constants
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.lang.ProcessBuilder.Redirect
import java.util.concurrent.TimeUnit.MINUTES
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class GitDescribeTest {

    @TempDir
    private lateinit var tempDir: File

    @Test
    fun `implements contract`() {
        Assertions.assertInstanceOf(
            GitContract.Describe::class.java,
            GitDescribe(mockk())
        )
    }

    @Test
    fun `GIVEN empty repository WHEN describe THEN describe is empty`() {
        // GIVEN
        val git = Git.init().setDirectory(tempDir).call()

        // WHEN
        val gitDescribe = GitDescribe(GitLoader.load(tempDir))
        val result = gitDescribe.describe("")

        // THEN
        assertNativeGitDescribe(result, tempDir)
        assertJGitDescribe(result, git)
        assertEquals(
            expected = "",
            actual = result
        )
    }

    @Test
    fun `GIVEN no tag WHEN describe THEN describe is hash`() {
        // GIVEN
        val git = Git.init().setDirectory(tempDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()

        // WHEN
        val gitDescribe = GitDescribe(GitLoader.load(tempDir))
        val result = gitDescribe.describe("")

        // THEN
        assertNativeGitDescribe(result, tempDir)
        assertJGitDescribe(result, git)

        assertTrue(
            actual = result.contains(HASH_SHORT_PATTERN.toRegex()),
            message = "$result does not match pattern: $HASH_SHORT_PATTERN"
        )
    }

    @Test
    fun `GIVEN with tag and no additional commit WHEN describe THEN describe is tag`() {
        // GIVEN
        val git = Git.init().setDirectory(tempDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()

        // WHEN
        val gitDescribe = GitDescribe(GitLoader.load(tempDir))
        val result = gitDescribe.describe("")

        // THEN
        assertNativeGitDescribe(result, tempDir)
        assertJGitDescribe(result, git)
        assertEquals(
            expected = "1.0.0",
            actual = result,
        )
    }

    @Test
    fun `GIVEN with tag and additional commit WHEN describe() THEN count up by one`() {
        // GIVEN
        val git = Git.init().setDirectory(tempDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()
        git.commit().setMessage("commit 2").call()

        // WHEN
        val gitDescribe = GitDescribe(GitLoader.load(tempDir))
        val result = gitDescribe.describe("")

        // THEN
        assertNativeGitDescribe(result, tempDir)
        assertJGitDescribe(result, git)
        val pattern = "1.0.0-1-$GIT_HASH_SHORT_PATTERN".toRegex()
        assertTrue(
            actual = result.contains(pattern),
            message = "$result does not match $pattern"
        )
    }

    @Test
    fun `GIVEN tag set on deep commit WHEN describe() THEN return description`() {
        // GIVEN
        val git = Git.init().setDirectory(tempDir).call()
        git.add().addFilepattern(".").call()
        var latestCommit = git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()

        val depth = 100
        for (i in 1..depth) {
            git.add().addFilepattern(".").call()
            latestCommit = git.commit().setMessage("commit-$i").call()
        }

        // WHEN
        val gitDescribe = GitDescribe(GitLoader.load(tempDir))
        val result = gitDescribe.describe("")

        // THEN
        assertNativeGitDescribe(result, tempDir)
        assertJGitDescribe(result, git)
        val pattern = "1.0.0-$depth-g${latestCommit.name.take(Constants.OBJECT_ID_ABBREV_STRING_LENGTH)}".toRegex()
        assertTrue(
            actual = result.contains(pattern),
            message = "$result does not match $pattern"
        )
    }

    @Test
    fun `GIVEN multiple tags on same commit WHEN describe() THEN annotated tag is chosen`() {
        // GIVEN
        val git = Git.init().setDirectory(tempDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(false).setName("1.0.0").call()
        git.tag().setAnnotated(true).setName("2.0.0").call()
        git.tag().setAnnotated(false).setName("3.0.0").call()

        // WHEN
        val gitDescribe = GitDescribe(GitLoader.load(tempDir))
        val result = gitDescribe.describe("")

        // THEN
        assertNativeGitDescribe(result, tempDir)
        assertJGitDescribe(result, git)
        assertEquals(
            expected = "2.0.0",
            actual = result
        )
    }

    @Test
    fun `GIVEN annotated tag is present with merge commit WHEN describe() THEN annotated tag is chosen`() {
        // GIVEN
        val git = Git.init().setDirectory(tempDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setName("1.0.0").setMessage("1.0.0").call()

        // create a new branch called "hotfix" that has a single commit and is tagged with "1.0.0-hotfix"
        val main = git.repository.fullBranch
        val hotfixBranch = git.branchCreate().setName("hotfix").call()
        git.checkout().setName(hotfixBranch.name).call()
        git.commit().setMessage("hot fix for issue").call()
        git.tag().setAnnotated(true)
            .setMessage("1.0.0-hotfix")
            .setName("1.0.0-hotfix")
            .call()

        // switch back to main branch and merge hotfix branch into main branch
        git.checkout().setName(main).call()
        git.merge().include(git.repository.refDatabase.findRef("hotfix"))
            .setFastForward(MergeCommand.FastForwardMode.NO_FF)
            .setMessage("merge commit")
            .call()

        // WHEN
        val gitDescribe = GitDescribe(GitLoader.load(tempDir))
        val result = gitDescribe.describe("")

        // THEN
        assertNativeGitDescribe(result, tempDir)
        assertJGitDescribe(result, git)
        val pattern = "1.0.0-2-$GIT_HASH_SHORT_PATTERN".toRegex()
        assertTrue(
            actual = result.contains(pattern),
            message = "$result does not match $pattern"
        )
    }

    @Test
    fun `GIVEN multiple tags with some having prefix WHEN describe() THEN only tag with prefix is shown`() {
        // GIVEN
        val prefix = "my-prefix@"
        val git = Git.init().setDirectory(tempDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("${prefix}1.0.0").setName("${prefix}1.0.0").call()
        git.commit().setMessage("commit 2").call()
        git.tag().setAnnotated(true).setMessage("1.1.0").setName("1.1.0").call()

        // WHEN
        val gitDescribe = GitDescribe(GitLoader.load(tempDir))
        val result = gitDescribe.describe(prefix)

        // THEN
        assertNativeGitDescribe(result, tempDir, prefix)
        assertJGitDescribe(result, git, prefix)
        val pattern = "my-prefix@1.0.0-1-$GIT_HASH_SHORT_PATTERN".toRegex()
        assertTrue(
            actual = result.contains(pattern),
            message = "$result does not match $pattern"
        )
    }

    private companion object {
        fun runNativeGitDescribe(folder: File, prefix: String = ""): String {
            val commands = mutableListOf<String>()
            commands.addAll("git describe --always".split(" "))
            if (prefix.isNotEmpty()) {
                commands.addAll(listOf("--match", "$prefix*"))
            }
            return ProcessBuilder(commands)
                .directory(folder)
                .redirectOutput(Redirect.PIPE)
                .redirectError(Redirect.PIPE)
                .start().also { it.waitFor(2, MINUTES) }
                .inputStream.bufferedReader().readText().replace("\n", "")
        }

        fun runJGitDescribe(git: Git, prefix: String = ""): String {
            return try {
                val describe = git.describe()
                if (prefix.isNotEmpty()) {
                    describe.setMatch("$prefix*")
                }
                describe
                    .setAlways(true)
                    .call()
            } catch (exception: RefNotFoundException) {
                ""
            }
        }

        fun assertNativeGitDescribe(actual: String, folder: File, prefix: String = "") {
            val nativeGitDescribe = runNativeGitDescribe(folder, prefix)
            assertEquals(
                expected = nativeGitDescribe,
                actual = actual,
                message = "Failed against nativeGitDescribe: $nativeGitDescribe vs actual: $actual"
            )
        }

        fun assertJGitDescribe(actual: String, git: Git, prefix: String = "") {
            val jGitDescribe = runJGitDescribe(git, prefix)
            assertEquals(
                expected = jGitDescribe,
                actual = actual,
                message = "Failed against jGitDescribe: $jGitDescribe vs actual: $actual"
            )
        }

        const val HASH_SHORT_PATTERN = "[a-z0-9]{${Constants.OBJECT_ID_ABBREV_STRING_LENGTH}}"
        const val GIT_HASH_SHORT_PATTERN = "g$HASH_SHORT_PATTERN"
    }
}
