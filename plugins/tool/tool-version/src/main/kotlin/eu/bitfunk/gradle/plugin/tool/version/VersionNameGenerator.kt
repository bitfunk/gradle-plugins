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

package eu.bitfunk.gradle.plugin.tool.version

import eu.upwolf.gradle.gitversion.VersionDetails
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.provideDelegate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class VersionNameGenerator(
    private val project: Project
) : VersionContract.Generator {

    override fun generateVersionName(): String {
        val details = loadVersionDetails()

        return when {
            details.branchName == null -> versionNameWithQualifier(details)
            patternNoQualifierBranch.matches(details.branchName) -> versionNameWithQualifier(details)
            patternFeatureBranch.matches(details.branchName) -> versionNameFeature(details)
            patternDependabotBranch.matches(details.branchName) -> versionNameDependabot(details)
            else -> throw UnsupportedOperationException("branch name not supported: ${details.branchName}")
        }
    }

    override fun generateVersionCode(): Int {
        val details = loadVersionDetails()

        return details.versionCode * 100 + details.commitDistance
    }

    override fun generateFeatureVersionCode(date: Date): Int {
        val timestamp = SimpleDateFormat("MMddHHmm", Locale.ENGLISH).format(date)
        return timestamp.toInt()
    }

    override fun generateVersionDetails(): String {
        val details = loadVersionDetails()

        return """
            VersionDetails(
               version = ${details.version}
               versionCode = ${details.versionCode}
               gitHash = ${details.gitHash}
               gitHashFull = ${details.gitHashFull}
               branchName = ${details.branchName}
               commitDistance = ${details.commitDistance}
               lastTag = ${details.lastTag}
               isClean = ${details.isCleanTag}
            )
        """.trimIndent()
    }

    private fun loadVersionDetails(): VersionDetails {
        val versionDetails: groovy.lang.Closure<VersionDetails> by project.extensions.extraProperties
        return versionDetails()
    }

    private fun versionNameWithQualifier(details: VersionDetails, name: String = ""): String {
        val version = if (!details.isCleanTag) {
            var versionCleaned = details.version.substringBefore(".dirty")
            if (details.commitDistance > 0) {
                versionCleaned = versionCleaned.substringBefore("-")
            }
            if (name.isBlank()) {
                "$versionCleaned-SNAPSHOT"
            } else {
                "$versionCleaned-$name-SNAPSHOT"
            }
        } else {
            details.version
        }

        return version.substringAfter("v")
    }

    private fun versionNameFeature(details: VersionDetails): String {
        var featureName = patternFeatureBranch.matchEntire(details.branchName)!!.groups[1]!!.value

        if (patternIssueNumber.matches(featureName)) {
            featureName = patternIssueNumber.matchEntire(featureName)!!.groups[1]!!.value
        }

        return versionNameWithQualifier(details, featureName)
    }

    private fun versionNameDependabot(details: VersionDetails): String {
        var dependabotName = patternDependabotBranch.matchEntire(details.branchName)!!.groups[1]!!.value

        dependabotName = dependabotName
            .replace("_", "-")
            .replace("/", "-")

        return versionNameWithQualifier(details, "bump-$dependabotName")
    }

    private companion object {
        val patternNoQualifierBranch = "main|release/.*".toRegex()
        val patternFeatureBranch = "feature/(.*)".toRegex()
        val patternDependabotBranch = "dependabot/(.*)".toRegex()
        val patternIssueNumber = "[A-Z]{2,8}-.*/(.*)".toRegex()
    }
}
