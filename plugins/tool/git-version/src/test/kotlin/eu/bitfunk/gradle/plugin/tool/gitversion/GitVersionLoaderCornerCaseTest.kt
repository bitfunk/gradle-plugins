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

import io.mockk.every
import io.mockk.mockk
import org.eclipse.jgit.api.DescribeCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GitVersionLoaderCornerCaseTest {

    @Test
    fun `GIVEN no tag and special commit number WHEN loadGitVersionInfo THEN version is hash`() {
        // GIVEN
        val gitHashFull = "7324508646fd6d8813e8f80a62d55efaabdd7aa5"
        val gitHash = "7324508"
        val git: Git = mockk()
        val describe: DescribeCommand = mockk()
        val repository: Repository = mockk()
        val masterRef: Ref = mockk()
        val headRef: Ref = mockk()
        val headObjectId: ObjectId = mockk()
        val status: Status = mockk()

        every { git.describe() } returns describe
        every { describe.setAlways(true) } returns describe
        every { describe.call() } returns gitHash

        every { git.repository } returns repository
        every { repository.branch } returns "master"

        every { repository.findRef("master") } returns masterRef
        every { masterRef.name } returns "refs/heads/master"

        every { repository.findRef("HEAD") } returns headRef
        every { headRef.objectId } returns headObjectId
        every { headObjectId.name } returns gitHashFull

        every { git.status().call() } returns status
        every { status.isClean } returns true

        // WHEN
        val result = GitVersionLoader(git, "").loadGitVersionInfo()

        // THEN
        assertEquals(
            expected = GitVersionInfo(
                version = "7324508",
                versionCode = -1,
                branchName = "master",
                gitHashFull = "7324508646fd6d8813e8f80a62d55efaabdd7aa5",
                gitHash = "7324508",
                lastTag = "unspecified",
                isCleanTag = true,
                commitDistance = 0,
            ),
            actual = result,
        )
    }
}
