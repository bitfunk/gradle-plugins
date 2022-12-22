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
import org.eclipse.jgit.api.MergeCommand
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.PersonIdent
import org.gradle.internal.impldep.org.joda.time.DateTime
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import java.util.Date
import kotlin.test.assertTrue

class GitVersionPluginFunctionalTest {

    @TempDir
    private lateinit var tempDir: File

    private lateinit var projectDir: File

    private lateinit var buildFile: File
    private lateinit var settingsFile: File
    private lateinit var gitIgnoreFile: File
    private lateinit var dirtyContentFile: File

    @BeforeEach
    fun setup() {
        projectDir = tempDir

        buildFile = File(projectDir, "build.gradle.kts")
        buildFile.createNewFile()
        settingsFile = File(projectDir, "settings.gradle.kts")
        settingsFile.createNewFile()
        settingsFile.writeText(SETTINGS_FILE_DEFAULT)
        gitIgnoreFile = File(projectDir, ".gitignore")
        gitIgnoreFile.createNewFile()
        gitIgnoreFile.writeText(".gradle\n")
        dirtyContentFile = File(projectDir, "dirty")
        dirtyContentFile.createNewFile()
    }

    @Test
    fun `GIVEN project without git folder WHEN plugin applied THEN fail with exception`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)

        // WHEN
        val runner = withRunner(projectDir)
            .buildAndFail()

        // THEN
        assertTrue(
            actual = runner.output.contains("Cannot find '.git' directory")
        )
    }

    @Test
    fun `git describe works when git repo is multiple levels up`() {
        // GIVEN
        val rootDir: File = tempDir
        projectDir = Files.createDirectories(rootDir.toPath().resolve("level1/level2")).toFile()
        buildFile = File(projectDir, "build.gradle.kts")
        buildFile.writeText(BUILD_FILE_DEFAULT)
        gitIgnoreFile.appendText("build")
        File(projectDir, "settings.gradle.kts").createNewFile()

        val git = Git.init().setDirectory(rootDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()

        // WHEN
        val result = withRunner(projectDir, "printGitVersion").build()

        // THEN
        val pattern = ":printGitVersion\n1.0.0\n"
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `git version can be applied on sub modules`() {
        // GIVEN
        val subModuleDir = Files.createDirectories(projectDir.toPath().resolve("submodule")).toFile()
        val subModuleBuildFile = File(subModuleDir, "build.gradle.kts")
        subModuleBuildFile.createNewFile()
        subModuleBuildFile.writeText(BUILD_FILE_DEFAULT)

        settingsFile.appendText(
            """
            include("submodule")
            """.trimIndent()
        )

        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()

        // WHEN
        val result = withRunner(projectDir, "printGitVersion").build()

        // THEN
        val pattern = ":printGitVersion\n1.0.0\n"
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `unspecified when no tags are present`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        Git.init().setDirectory(projectDir).call()

        // WHEN
        val result = withRunner(projectDir, "printGitVersion").build()

        // THEN
        val pattern = ":printGitVersion\nunspecified\n"
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `git describe when annotated tag is present`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        gitIgnoreFile.appendText("build")
        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()

        // WHEN
        val result = withRunner(projectDir, "printGitVersion").build()

        // THEN
        val pattern = ":printGitVersion\n1.0.0\n"
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `GIVEN lightweight tag WHEN describe THEN tag is ignored`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        gitIgnoreFile.appendText("build")
        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(false).setName("1.0.0").call()

        // WHEN
        val result = withRunner(projectDir, "printGitVersion").build()

        // THEN
        val pattern = ":printGitVersion\n$HASH_SHORT_PATTERN\n".toRegex()
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `git describe when annotated tag is present with merge commit`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        gitIgnoreFile.appendText("build")

        // create repository with a single commit tagged as 1.0.0
        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()

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
        val result = withRunner(projectDir, "printGitVersion").build()

        // THEN
        val pattern = ":printGitVersion\n1.0.0-2-g[a-z0-9]{7}\n".toRegex()
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `git describe when annotated tag is present after merge commit`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        gitIgnoreFile.appendText("build")

        // create repository with a single commit tagged as 1.0.0
        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()

        // create a new branch called "hotfix" that has a single commit and is tagged with "1.0.0-hotfix"
        val main = git.repository.fullBranch
        val hotfixBranch = git.branchCreate().setName("hotfix").call()
        git.checkout().setName(hotfixBranch.name).call()
        git.commit().setMessage("hot fix for issue").call()
        git.tag().setAnnotated(true).setMessage("1.0.0-hotfix").setName("1.0.0-hotfix").call()

        // switch back to main branch and merge hotfix branch into main branch
        git.checkout().setName(main).call()
        git.merge().include(git.repository.refDatabase.findRef("hotfix"))
            .setFastForward(MergeCommand.FastForwardMode.NO_FF).setMessage("merge commit").call()

        // tag merge commit on main branch as 2.0.0
        git.tag().setAnnotated(true).setMessage("2.0.0").setName("2.0.0").call()

        // WHEN
        val result = withRunner(projectDir, "printGitVersion").build()

        // THEN
        val pattern = ":printGitVersion\n2.0.0\n"
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `git describe and dirty when annotated tag is present and dirty content`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        gitIgnoreFile.appendText("build")
        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()
        dirtyContentFile.appendText("dirty-content")

        // WHEN
        val result = withRunner(projectDir, "printGitVersion").build()

        // THEN
        val pattern = ":printGitVersion\n1.0.0.dirty\n"
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `version details on commit with a tag`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        gitIgnoreFile.appendText("build")

        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()

        // WHEN
        val result = withRunner(projectDir, "printGitVersionInfo").build()

        // THEN
        val pattern =
            ":printGitVersionInfo\n1.0.0\n10000\nmaster\n[a-z0-9]{40}\n$HASH_SHORT_PATTERN\n1.0.0\ntrue\n0\n".toRegex()
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `version details can be accessed using extra properties method`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        buildFile.appendText(
            """
                tasks.create("printVersionDetails") {
                    doLast {
                        val gitVersion = gitVersion()
                        val gitLastTag = gitVersionInfo().lastTag

                        println(gitVersion)
                        println(gitLastTag)
                    }
                }
            """.trimIndent()
        )
        gitIgnoreFile.appendText("build")

        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        val sha = git.commit().setMessage("initial commit").call().name.subSequence(0, 7)

        // WHEN
        val result = withRunner(projectDir, "printVersionDetails").build()

        // THEN
        val pattern = ":printVersionDetails\n${sha}\nunspecified\n"
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `version details when commit distance to tag is greater 0`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        buildFile.appendText(
            """
                tasks.create("printVersionDetails") {
                    doLast {
                        val gitVersionInfo = gitVersionInfo()

                        println(gitVersionInfo.lastTag)
                        println(gitVersionInfo.commitDistance)
                        println(gitVersionInfo.gitHash)
                        println(gitVersionInfo.branchName)
                        println(gitVersionInfo.isCleanTag)
                    }
                }
            """.trimIndent()
        )
        gitIgnoreFile.appendText("build")

        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()
        git.commit().setMessage("commit 2").call()

        // WHEN
        val result = withRunner(projectDir, "printVersionDetails").build()

        // THEN
        val pattern = ":printVersionDetails\n1.0.0\n1\n$HASH_SHORT_PATTERN\nmaster\nfalse\n".toRegex()
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `isCleanTag should be false when repo dirty on a tag checkout`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        buildFile.appendText(
            """
            tasks.create("printVersionDetails") {
                doLast {
                    val gitVersionInfo = gitVersionInfo()
                    println(gitVersionInfo.isCleanTag)
                }
            }
            """.trimIndent()
        )
        gitIgnoreFile.appendText("build")

        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        dirtyContentFile.appendText("dirty-content")

        // WHEN
        val result = withRunner(projectDir, "printVersionDetails").build()

        // THEN
        val pattern = ":printVersionDetails\nfalse\n"
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `version details when detached HEAD mode`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        buildFile.appendText(
            """
                tasks.create("printVersionDetails") {
                    doLast {
                        val gitVersionInfo = gitVersionInfo()

                        println(gitVersionInfo.lastTag)
                        println(gitVersionInfo.commitDistance)
                        println(gitVersionInfo.gitHash)
                        println(gitVersionInfo.branchName)
                    }
                }
            """.trimIndent()
        )
        gitIgnoreFile.appendText("build")

        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        val commit1 = git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()
        git.commit().setMessage("commit 2").call()
        git.checkout().setName(commit1.id.name).call()

        // WHEN
        val result = withRunner(projectDir, "printVersionDetails").build()

        // THEN
        val pattern = ":printVersionDetails\n1.0.0\n0\n$HASH_SHORT_PATTERN\nunspecified\n".toRegex()
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `version filters out tags not matching prefix and strips prefix`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        buildFile.appendText(
            """
                gitVersionConfig {
                    prefix.set("my-prefix@")
                }

                tasks.create("printVersionDetails") {
                    doLast {
                        val gitVersionInfo = gitVersionInfo()

                        println(gitVersionInfo.lastTag)
                    }
                }
            """.trimIndent()
        )
        gitIgnoreFile.appendText("build")

        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("my-prefix@1.0.0").setName("my-prefix@1.0.0").call()
        git.commit().setMessage("commit 2").call()
        git.tag().setAnnotated(true).setMessage("1.1.0").setName("1.1.0").call()

        // WHEN
        val result = withRunner(projectDir, "printVersionDetails").build()

        // THEN
        val pattern = ":printVersionDetails\n1.0.0\n"
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `git describe with commit after annotated tag`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        gitIgnoreFile.appendText("build")

        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()
        dirtyContentFile.appendText("dirty-content")
        git.add().addFilepattern(".").call()
        val latestCommit = git.commit().setMessage("added some stuff").call()

        // WHEN
        val result = withRunner(projectDir, "printGitVersion").build()
        val commitSha = latestCommit.name.take(Constants.OBJECT_ID_ABBREV_STRING_LENGTH)

        // THEN
        val pattern = ":printGitVersion\n1.0.0-1-g$commitSha\n"
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `GIVEN commit after lightweight tag WHEN describe THEN tag ignored`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        gitIgnoreFile.appendText("build")

        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(false).setName("1.0.0").call()
        dirtyContentFile.appendText("dirty-content")
        git.add().addFilepattern(".").call()
        val latestCommit = git.commit().setMessage("added some stuff").call()

        // WHEN
        val result = withRunner(projectDir, "printGitVersion").build()
        val commitSha = latestCommit.name.take(Constants.OBJECT_ID_ABBREV_STRING_LENGTH)

        // THEN
        val pattern = ":printGitVersion\n$commitSha\n"
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `test subproject version`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        buildFile.appendText(
            """
                subprojects {
                    apply(plugin = "eu.bitfunk.gradle.plugin.tool.gitversion")
                    version = gitVersion()
                }
            """.trimIndent()
        )

        settingsFile.appendText("include(\"sub\")")

        gitIgnoreFile.appendText("build\n")
        gitIgnoreFile.appendText("sub\n")

        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()

        val subDir = Files.createDirectory(tempDir.toPath().resolve("sub")).toFile()
        val subGit = Git.init().setDirectory(subDir).call()
        val subDirty = File(subDir, "subDirty")
        subDirty.createNewFile()
        subGit.add().addFilepattern(".").call()
        subGit.commit().setMessage("initial commit sub").call()
        subGit.tag().setAnnotated(true).setMessage("8.8.8").setName("8.8.8").call()

        // WHEN
        val result = withRunner(projectDir, "printGitVersion", ":sub:printGitVersion").build()

        // THEN
        assertTrue(
            result.output.contains(":printGitVersion\n1.0.0\n")
        )
        assertTrue(
            result.output.contains(":sub:printGitVersion\n8.8.8\n")
        )
    }

    @Test
    fun `test multiple tags on same commit - annotated tag is chosen`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        buildFile.appendText(
            """
                subprojects {
                    apply(plugin = "eu.bitfunk.gradle.plugin.tool.gitversion")
                    version = gitVersion()
                }
            """.trimIndent()
        )
        gitIgnoreFile.appendText("build")

        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(false).setName("1.0.0").call()
        git.tag().setAnnotated(true).setName("2.0.0").call()
        git.tag().setAnnotated(false).setName("3.0.0").call()

        // WHEN
        val result = withRunner(projectDir, "printGitVersion").build()

        // THEN
        val pattern = ":printGitVersion\n2.0.0\n"
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `test multiple tags on same commit - most recent annotated tag`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        buildFile.appendText(
            """
                subprojects {
                    apply(plugin = "eu.bitfunk.gradle.plugin.tool.gitversion")
                    version = gitVersion()
                }
            """.trimIndent()
        )
        gitIgnoreFile.appendText("build")

        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        val ident = PersonIdent("name", "emai2l@example.com")
        git.tag().setAnnotated(true).setTagger(PersonIdent(ident, DateTime.now().minusDays(2).toDate()))
            .setName("1.0.0").call()
        git.tag().setAnnotated(true).setTagger(PersonIdent(ident, Date())).setName("2.0.0").call()
        git.tag().setAnnotated(true).setTagger(PersonIdent(ident, DateTime.now().minusDays(1).toDate()))
            .setName("3.0.0").call()

        // WHEN
        val result = withRunner(projectDir, "printGitVersion").build()

        // THEN
        val pattern = ":printGitVersion\n2.0.0\n"
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `GIVEN multiple unannotated tags on same commit WHEN describe THEN not tag is chosen`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        buildFile.appendText(
            """
                subprojects {
                    apply(plugin = "eu.bitfunk.gradle.plugin.tool.gitversion")
                    version = gitVersion()
                }
            """.trimIndent()
        )
        gitIgnoreFile.appendText("build")

        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(false).setName("2.0.0").call()
        git.tag().setAnnotated(false).setName("1.0.0").call()
        git.tag().setAnnotated(false).setName("3.0.0").call()

        // WHEN
        val result = withRunner(projectDir, "printGitVersion").build()

        // THEN
        val pattern = ":printGitVersion\n$HASH_SHORT_PATTERN\n".toRegex()
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    @Test
    fun `test tag set on deep commit`() {
        // GIVEN
        buildFile.writeText(BUILD_FILE_DEFAULT)
        gitIgnoreFile.appendText("build")

        val git = Git.init().setDirectory(projectDir).call()
        git.add().addFilepattern(".").call()
        var latestCommit = git.commit().setMessage("initial commit").call()
        git.tag().setAnnotated(true).setMessage("1.0.0").setName("1.0.0").call()

        val depth = 100
        for (i in 1..depth) {
            git.add().addFilepattern(".").call()
            latestCommit = git.commit().setMessage("commit-$i").call()
        }

        // WHEN
        val result = withRunner(projectDir, "printGitVersion").build()
        val commitSha = latestCommit.name.take(Constants.OBJECT_ID_ABBREV_STRING_LENGTH)

        // THEN
        val pattern = ":printGitVersion\n1.0.0-$depth-g$commitSha\n"
        assertTrue(
            actual = result.output.contains(pattern),
            message = "${result.output} does not match $pattern"
        )
    }

    private companion object {
        const val HASH_SHORT_PATTERN = "[a-z0-9]{${Constants.OBJECT_ID_ABBREV_STRING_LENGTH}}"
        const val GIT_HASH_SHORT_PATTERN = "g$HASH_SHORT_PATTERN"

        private fun withRunner(projectDir: File, vararg tasks: String): GradleRunner {
            val arguments = mutableListOf("--stacktrace")
            arguments.addAll(tasks)

            return GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments(arguments)
        }

        val BUILD_FILE_DEFAULT = """
                import eu.bitfunk.gradle.plugin.tool.gitversion.gitVersion
                import eu.bitfunk.gradle.plugin.tool.gitversion.gitVersionInfo

                plugins {
                    id("eu.bitfunk.gradle.plugin.tool.gitversion")
                }

                version = gitVersion()

        """.trimIndent()

        val SETTINGS_FILE_DEFAULT = """
            rootProject.name = "gradle-plugin-tool-gitversion-test"

        """.trimIndent()
    }
}
