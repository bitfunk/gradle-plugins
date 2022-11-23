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
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.RepositoryBuilder
import java.io.File
import java.io.IOException
import java.text.ParseException

internal object GitLoader : GitContract.Loader {

    override fun load(projectDir: File): Git {
        val gitRootDir = getRootGitDir(projectDir)
        val dotGitFile = File(gitRootDir.path)
        return if (dotGitFile.isFile) {
            val gitSubmoduleFolder = getSubmoduleFolder(dotGitFile, projectDir.parent)
            val builder = RepositoryBuilder()
            builder.workTree = File(gitRootDir.parent)
            builder.gitDir = gitSubmoduleFolder
            Git.wrap(builder.build())
        } else {
            Git.wrap(FileRepository(gitRootDir))
        }
    }

    @Throws(ParseException::class, IOException::class)
    private fun getSubmoduleFolder(dotGitFile: File, parentDir: String): File {
        val pair = dotGitFile.readText()
            .split(SPLIT_REGEX, limit = 2)
            .toTypedArray()
        if (pair.size != 2) {
            throw ParseException(dotGitFile.toString(), 0)
        }
        if (pair[0].trim { it <= WHITESPACE } != GIT_FILE_DIR) {
            throw ParseException(pair[0], 0)
        }
        if (pair[1].trim { it <= WHITESPACE }.contains(LINEBREAK)) {
            throw ParseException(pair[1], pair[1].indexOf(LINEBREAK))
        }
        val gitDirStr = pair[1].trim { it <= WHITESPACE }
        return if (!File(gitDirStr).isAbsolute) {
            File(File(parentDir), gitDirStr)
        } else {
            File(gitDirStr)
        }
    }

    private fun getRootGitDir(currentRoot: File): File {
        val rootGitDir = scanForRootGitDir(currentRoot)
        require(rootGitDir.exists()) { "Cannot find '$GIT_FILE_NAME' directory" }
        return rootGitDir
    }

    private fun scanForRootGitDir(currentRoot: File): File {
        val gitDir = File(currentRoot, GIT_FILE_NAME)
        if (gitDir.exists()) {
            return gitDir
        }

        // stop at the root directory, return non-existing File object;
        return if (currentRoot.parentFile == null) {
            gitDir
        } else {
            scanForRootGitDir(
                currentRoot.parentFile
            )
        }
    }

    private const val GIT_FILE_NAME = ".git"
    private const val GIT_FILE_DIR = "gitdir"
    private const val WHITESPACE = ' '
    private const val LINEBREAK = "\n"
    private val SPLIT_REGEX = ":".toRegex()
}
