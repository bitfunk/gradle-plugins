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
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Calendar

internal class VersionNameGeneratorTest {

    private lateinit var project: Project

    private lateinit var testSubject: VersionNameGenerator

    @BeforeEach
    fun setup() {
        project = mockk()

        testSubject = VersionNameGenerator(project)
    }

    @Test
    fun plugin_implements_contract() {
        assertInstanceOf(
            VersioningContract.Generator::class.java,
            testSubject
        )
    }

    @Test
    fun `GIVEN unsupported branch name WHEN generateVersionName() THEN throw exception`() {
        // GIVEN
        val versionInfo: GitVersionInfo = mockLoadGitVersionInfo()
        every { versionInfo.branchName } returns "unsupported"

        // WHEN/THEN
        assertThrows(UnsupportedOperationException::class.java) {
            testSubject.generateVersionName()
        }
    }

    @Test
    fun `GIVEN null branch WHEN generateVersionName() THEN semver version`() {
        // GIVEN
        val versionInfo: GitVersionInfo = mockLoadGitVersionInfo()
        every { versionInfo.branchName } returns UNDEFINED
        every { versionInfo.isCleanTag } returns true
        every { versionInfo.version } returns "1.0.0"

        // WHEN
        val result = testSubject.generateVersionName()

        // THEN
        assertEquals(
            "1.0.0",
            result
        )
    }

    @Test
    fun `GIVEN main branch and tag version WHEN generateVersionName() THEN semver version`() {
        // GIVEN
        val versionInfo: GitVersionInfo = mockLoadGitVersionInfo()
        every { versionInfo.branchName } returns "main"
        every { versionInfo.isCleanTag } returns true
        every { versionInfo.version } returns "1.0.0"

        // WHEN
        val result = testSubject.generateVersionName()

        // THEN
        assertEquals(
            "1.0.0",
            result
        )
    }

    @Test
    fun `GIVEN main branch and tag version with starting v WHEN generateVersionName() THEN semver version`() {
        // GIVEN
        val versionInfo: GitVersionInfo = mockLoadGitVersionInfo()
        every { versionInfo.branchName } returns "main"
        every { versionInfo.isCleanTag } returns true
        every { versionInfo.version } returns "v1.0.0"

        // WHEN
        val result = testSubject.generateVersionName()

        // THEN
        assertEquals(
            "1.0.0",
            result
        )
    }

    @Test
    fun `GIVEN main branch and dirty version WHEN generateVersionName() THEN semver snapshot version`() {
        // GIVEN
        val versionInfo: GitVersionInfo = mockLoadGitVersionInfo()
        every { versionInfo.branchName } returns "main"
        every { versionInfo.isCleanTag } returns false
        every { versionInfo.version } returns "1.0.0"
        every { versionInfo.commitDistance } returns 5

        // WHEN
        val result = testSubject.generateVersionName()

        // THEN
        assertEquals(
            "1.0.0-SNAPSHOT",
            result
        )
    }

    @Test
    fun `GIVEN release branch and clean version WHEN generateVersionName() THEN semver version`() {
        // GIVEN
        val versionInfo: GitVersionInfo = mockLoadGitVersionInfo()
        every { versionInfo.branchName } returns "release/1.0.1"
        every { versionInfo.isCleanTag } returns false
        every { versionInfo.version } returns "1.0.0"
        every { versionInfo.commitDistance } returns 4

        // WHEN
        val result = testSubject.generateVersionName()

        // THEN
        assertEquals(
            "1.0.0-SNAPSHOT",
            result
        )
    }

    @Test
    fun `GIVEN feature branch and dirty version WHEN generateVersionName() THEN semver version`() {
        // GIVEN
        val versionInfo: GitVersionInfo = mockLoadGitVersionInfo()
        every { versionInfo.branchName } returns "feature/add-some-code"
        every { versionInfo.isCleanTag } returns false
        every { versionInfo.version } returns "1.0.0"
        every { versionInfo.commitDistance } returns 2

        // WHEN
        val result = testSubject.generateVersionName()

        // THEN
        assertEquals(
            "1.0.0-add-some-code-SNAPSHOT",
            result
        )
    }

    @Test
    fun `GIVEN feature branch, dirty version, zero commits WHEN generateVersionName() THEN semver version`() {
        // GIVEN
        val versionInfo: GitVersionInfo = mockLoadGitVersionInfo()
        every { versionInfo.branchName } returns "feature/add-some-code"
        every { versionInfo.isCleanTag } returns false
        every { versionInfo.version } returns "1.0.0"
        every { versionInfo.commitDistance } returns 0

        // WHEN
        val result = testSubject.generateVersionName()

        // THEN
        assertEquals(
            "1.0.0-add-some-code-SNAPSHOT",
            result
        )
    }

    @Test
    fun `GIVEN feature branch with ticket and dirty version WHEN generateVersionName() THEN semver version`() {
        // GIVEN
        val versionInfo: GitVersionInfo = mockLoadGitVersionInfo()
        every { versionInfo.branchName } returns "feature/ABC-123/add-some-numbers"
        every { versionInfo.isCleanTag } returns false
        every { versionInfo.version } returns "1.0.0"
        every { versionInfo.commitDistance } returns 1

        // WHEN
        val result = testSubject.generateVersionName()

        // THEN
        assertEquals(
            "1.0.0-add-some-numbers-SNAPSHOT",
            result
        )
    }

    @Test
    fun `GIVEN dependabot branch and clean version WHEN generateVersionName() THEN semver version`() {
        // GIVEN
        val versionInfo: GitVersionInfo = mockLoadGitVersionInfo()
        every { versionInfo.branchName } returns "dependabot/library_1.0.0"
        every { versionInfo.isCleanTag } returns false
        every { versionInfo.version } returns "1.0.0"
        every { versionInfo.commitDistance } returns 1

        // WHEN
        val result = testSubject.generateVersionName()

        // THEN
        assertEquals(
            "1.0.0-bump-library-1.0.0-SNAPSHOT",
            result
        )
    }

    @Test
    fun `GIVEN renovate configure branch WHEN generateVersionName() THEN `() {
        // GIVEN
        val versionInfo: GitVersionInfo = mockLoadGitVersionInfo()
        every { versionInfo.branchName } returns "renovate/configure"
        every { versionInfo.isCleanTag } returns false
        every { versionInfo.version } returns "1.0.0"
        every { versionInfo.commitDistance } returns 1

        // WHEN
        val result = testSubject.generateVersionName()

        // THEN
        assertEquals(
            "1.0.0-renovate-configure-SNAPSHOT",
            result
        )
    }

    @Test
    fun `GIVEN versionCode, no commitDistance WHEN generateVersionCode() THEN versionCode shifted `() {
        // GIVEN
        val versionInfo: GitVersionInfo = mockLoadGitVersionInfo()
        every { versionInfo.versionCode } returns 101010
        every { versionInfo.commitDistance } returns 0

        // WHEN
        val result = testSubject.generateVersionCode()

        // THEN
        assertEquals(
            10101000,
            result
        )
    }

    @Test
    fun `GIVEN versionCode, commitDistance WHEN generateVersionCode() THEN versionCode shifted `() {
        // GIVEN
        val versionInfo: GitVersionInfo = mockLoadGitVersionInfo()
        every { versionInfo.versionCode } returns 1415
        every { versionInfo.commitDistance } returns 23

        // WHEN
        val result = testSubject.generateVersionCode()

        // THEN
        assertEquals(
            141523,
            result
        )
    }

    @Test
    fun `GIVEN date WHEN generateFeatureVersionCode() THEN timestamp`() {
        // GIVEN
        val date = Calendar.getInstance().also {
            it.set(2022, 5, 10, 12, 53)
        }.time

        // WHEN
        val result = testSubject.generateFeatureVersionCode(date)

        // THEN
        assertEquals(
            6101253,
            result
        )
    }

    @Test
    fun `GIVEN versionDetails WHEN generateVersionDetails() THEN all details printed`() {
        // GIVEN
        val versionInfo: GitVersionInfo = mockLoadGitVersionInfo()
        every { versionInfo.version } returns "1.0.0"
        every { versionInfo.versionCode } returns 100000
        every { versionInfo.gitHash } returns "gitHash"
        every { versionInfo.gitHashFull } returns "gitHashFull"
        every { versionInfo.branchName } returns "branchName"
        every { versionInfo.commitDistance } returns 111
        every { versionInfo.lastTag } returns "0.9.4"
        every { versionInfo.isCleanTag } returns false

        // WHEN
        val result = testSubject.generateVersionDetails()

        // THEN
        assertEquals(
            """
            VersionDetails(
               version = 1.0.0
               versionCode = 100000
               gitHash = gitHash
               gitHashFull = gitHashFull
               branchName = branchName
               commitDistance = 111
               lastTag = 0.9.4
               isClean = false
            )
            """.trimIndent(),
            result
        )
    }

    private fun mockLoadGitVersionInfo(): GitVersionInfo {
        val extraPropertiesExtension: ExtraPropertiesExtension = mockk()
        val versionDetailsClosure: groovy.lang.Closure<GitVersionInfo> = mockk()
        val versionInfo: GitVersionInfo = mockk()
        every { project.extensions.extraProperties } returns extraPropertiesExtension
        every { extraPropertiesExtension.has("gitVersionInfo") } returns true
        every { extraPropertiesExtension.get("gitVersionInfo") } returns versionDetailsClosure
        every { versionDetailsClosure.call() } returns versionInfo
        return versionInfo
    }

    private companion object {
        private const val UNDEFINED = "unspecified"
    }
}
