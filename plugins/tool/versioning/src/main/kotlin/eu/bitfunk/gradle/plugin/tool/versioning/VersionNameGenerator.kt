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

package eu.bitfunk.gradle.plugin.tool.versioning

import eu.bitfunk.gradle.plugin.tool.gitversion.GitVersionInfo
import eu.bitfunk.gradle.plugin.tool.gitversion.gitVersionInfo
import eu.bitfunk.gradle.plugin.tool.versioning.VersioningContract.Generator
import org.gradle.api.Project
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class VersionNameGenerator(
    private val project: Project
) : Generator {

    override fun generateVersionName(): String {
        val versionInfo = project.gitVersionInfo()

        return when {
            versionInfo.branchName == UNDEFINED -> versionNameWithQualifier(versionInfo)
            patternNoQualifierBranch.matches(versionInfo.branchName) -> versionNameWithQualifier(versionInfo)
            patternFeatureBranch.matches(versionInfo.branchName) -> versionNameFeature(versionInfo)
            patternDependabotBranch.matches(versionInfo.branchName) -> versionNameDependabot(versionInfo)
            patternRenovateBranch.matches(versionInfo.branchName) -> versionNameRenovate(versionInfo)
            else -> throw UnsupportedOperationException("branch name not supported: ${versionInfo.branchName}")
        }
    }

    override fun generateVersionCode(): Int {
        val versionInfo = project.gitVersionInfo()

        return versionInfo.versionCode * VERSION_CODE_SHIFT + versionInfo.commitDistance
    }

    override fun generateFeatureVersionCode(date: Date): Int {
        val timestamp = SimpleDateFormat("MMddHHmm", Locale.ENGLISH).format(date)
        return timestamp.toInt()
    }

    override fun generateVersionDetails(): String {
        val versionInfo = project.gitVersionInfo()

        return """
            VersionDetails(
               version = ${versionInfo.version}
               versionCode = ${versionInfo.versionCode}
               gitHash = ${versionInfo.gitHash}
               gitHashFull = ${versionInfo.gitHashFull}
               branchName = ${versionInfo.branchName}
               commitDistance = ${versionInfo.commitDistance}
               lastTag = ${versionInfo.lastTag}
               isClean = ${versionInfo.isCleanTag}
            )
        """.trimIndent()
    }

    private fun versionNameWithQualifier(versionInfo: GitVersionInfo, name: String = ""): String {
        val version = if (!versionInfo.isCleanTag) {
            var versionCleaned = versionInfo.version.substringBefore(".dirty")
            if (versionInfo.commitDistance > 0) {
                versionCleaned = versionCleaned.substringBefore("-")
            }
            if (name.isBlank()) {
                "$versionCleaned-SNAPSHOT"
            } else {
                "$versionCleaned-$name-SNAPSHOT"
            }
        } else {
            versionInfo.version
        }

        return if (version.startsWith("v")) {
            version.substring(1)
        } else {
            version
        }
    }

    private fun versionNameFeature(versionInfo: GitVersionInfo): String {
        var featureName = patternFeatureBranch.matchEntire(versionInfo.branchName)!!.groups[1]!!.value

        if (patternIssueNumber.matches(featureName)) {
            featureName = patternIssueNumber.matchEntire(featureName)!!.groups[1]!!.value
        }

        return versionNameWithQualifier(versionInfo, featureName)
    }

    private fun versionNameDependabot(versionInfo: GitVersionInfo): String {
        var dependabotName = patternDependabotBranch.matchEntire(versionInfo.branchName)!!.groups[1]!!.value

        dependabotName = dependabotName
            .replace("_", "-")
            .replace("/", "-")

        return versionNameWithQualifier(versionInfo, "bump-$dependabotName")
    }

    private fun versionNameRenovate(versionInfo: GitVersionInfo): String {
        var renovateName = patternRenovateBranch.matchEntire(versionInfo.branchName)!!.groups[1]!!.value

        renovateName = renovateName
            .replace("_", "-")
            .replace("/", "-")

        if (renovateName == "configure") {
            renovateName = "renovate-$renovateName"
        } else {
            renovateName = "renovate-bump-$renovateName"
        }

        return versionNameWithQualifier(versionInfo, renovateName)
    }

    private companion object {
        val patternNoQualifierBranch = "main|release/.*".toRegex()
        val patternFeatureBranch = "feature/(.*)".toRegex()
        val patternDependabotBranch = "dependabot/(.*)".toRegex()
        val patternRenovateBranch = "renovate/(.*)".toRegex()
        val patternIssueNumber = "[A-Z]{2,8}-.*/(.*)".toRegex()

        private const val UNDEFINED = "unspecified"
        const val VERSION_CODE_SHIFT = 100
    }
}
