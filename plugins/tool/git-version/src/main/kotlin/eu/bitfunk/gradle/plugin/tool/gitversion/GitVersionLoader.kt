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

import eu.bitfunk.gradle.plugin.tool.gitversion.internal.git.GitDescribe
import net.swiftzer.semver.SemVer
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import java.util.regex.Pattern

public class GitVersionLoader(
    private val git: Git,
    private val prefix: String
) : GitVersionContract.Loader {

    init {
        assert(prefix.isEmpty() || prefix.matches(PREFIX_REGEX)) {
            "Specified prefix `$prefix` does not match the allowed format regex `${PREFIX_REGEX}`."
        }
    }

    @Volatile
    private var cachedVersionInfo: GitVersionInfo? = null

    override fun loadGitVersionInfo(): GitVersionInfo {
        if (cachedVersionInfo != null) {
            return cachedVersionInfo as GitVersionInfo
        }
        val gitDescribe = GitDescribe(git).describe(prefix)

        return mapToVersionDetails(gitDescribe).also {
            cachedVersionInfo = it
        }
    }

    private fun mapToVersionDetails(gitDescribe: String): GitVersionInfo {
        val description = gitDescribe.replaceFirst(("^$prefix").toRegex(), "")
        return GitVersionInfo(
            version = version(description),
            versionCode = versionCode(description),
            branchName = branchName(),
            gitHashFull = gitHashFull(),
            gitHash = gitHash(),
            lastTag = lastTag(description),
            isCleanTag = isClean() && isPlainTag(description),
            commitDistance = commitDistance(description),
        )
    }

    private fun version(gitDescribe: String): String {
        return if (gitDescribe.isEmpty()) {
            UNDEFINED
        } else {
            gitDescribe + if (!isClean()) ".dirty" else ""
        }
    }

    private fun versionCode(description: String): Int {
        return if (description.isEmpty()) {
            -1
        } else {
            var versionDescription = description
            if (description.startsWith("v")) versionDescription = description.substring(1)
            try {
                val version: SemVer = SemVer.parse(versionDescription)
                return version.major * MAJOR_SHIFT + version.minor * MINOR_SHIFT + version.patch
            } catch (exception: Exception) {
                return -1
            }
        }
    }

    private fun branchName(): String {
        val ref: Ref = git.repository.findRef(git.repository.branch) ?: return UNDEFINED
        return ref.name.substring(Constants.R_HEADS.length)
    }

    private fun gitHashFull(): String {
        val objectId: ObjectId = git.repository.findRef(Constants.HEAD).objectId ?: return UNDEFINED
        return objectId.name
    }

    private fun gitHash(): String {
        val gitHashFull = gitHashFull()
        return if (gitHashFull == UNDEFINED) UNDEFINED
        else gitHashFull.substring(0, Constants.OBJECT_ID_ABBREV_STRING_LENGTH)
    }

    private fun lastTag(description: String): String {
        if (description.isEmpty() || !isTag(description)) return UNDEFINED
        if (isPlainTag(description)) return description

        val match = Pattern.compile(LAST_TAG_PATTERN).matcher(description)
        return if (match.matches()) match.group(1) else UNDEFINED
    }

    private fun isClean(): Boolean {
        return git.status().call().isClean
    }

    private fun isPlainTag(description: String): Boolean {
        return !CLEAN_TAG_REGEX.matches(description)
    }

    private fun isTag(description: String): Boolean {
        return TAG_REGEX.matches(description)
    }

    private fun commitDistance(description: String): Int {
        if (isPlainTag(description)) return 0

        val match = Pattern.compile(COMMIT_DISTANCE_PATTERN).matcher(description)
        assert(match.matches()) { "Cannot get commit distance for description: '${description}'" }
        return match.group(2).toInt()
    }

    private companion object {
        private val PREFIX_REGEX = "[/@]?([A-Za-z]+[/@-])+".toRegex()
        private val TAG_REGEX = "(.*)(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(.*)".toRegex()
        private val CLEAN_TAG_REGEX = ".*g.?[0-9a-fA-F]{3,}".toRegex()
        private const val COMMIT_DISTANCE_PATTERN = "(.*)-([0-9]+)-g.?[0-9a-fA-F]{3,}"
        private const val LAST_TAG_PATTERN = "(.*)-([0-9]+)-g.?[0-9a-fA-F]{3,}"
        private const val UNDEFINED = "unspecified"
        private const val MAJOR_SHIFT = 10000
        private const val MINOR_SHIFT = 100
    }
}
