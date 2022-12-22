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

import eu.bitfunk.gradle.plugin.tool.gitversion.GitVersionContract.Extension
import eu.bitfunk.gradle.plugin.tool.gitversion.internal.git.GitLoader
import groovy.lang.Closure
import org.eclipse.jgit.api.Git
import org.gradle.api.Plugin
import org.gradle.api.Project

public class GitVersionPlugin : Plugin<Project>, GitVersionContract.Plugin {

    override fun apply(target: Project) {
        val extension = addExtension(target)
        val git: Git = GitLoader.load(target.projectDir)
        addExtraProperties(target, extension, git)
        configureVersionTasks(target)
    }

    override fun addExtension(project: Project): Extension = with(project) {
        val extension = extensions.create("gitVersionConfig", GitVersionPluginExtension::class.java)
        extension.prefix.convention("")

        return extension
    }

    override fun addExtraProperties(project: Project, extension: Extension, git: Git): Unit = with(project) {
        extensions.extraProperties["gitVersion"] =
            object : Closure<String>(this, this) {
                fun doCall(): String {
                    return GitVersionLoader(git, extension.prefix.get())
                        .loadGitVersionInfo().version
                }
            }

        extensions.extraProperties["gitVersionInfo"] =
            object : Closure<GitVersionInfo>(this, this) {
                fun doCall(): GitVersionInfo {
                    return GitVersionLoader(git, extension.prefix.get())
                        .loadGitVersionInfo()
                }
            }
    }

    override fun configureVersionTasks(project: Project): Unit = with(project) {
        tasks.create("printGitVersion") {
            group = "versioning"
            description = "Prints the git version to standard out"

            doLast {
                println(gitVersion())
            }
        }

        tasks.create("printGitVersionInfo") {
            group = "versioning"
            description = "Prints the project's git version info to standard out"

            doLast {
                val gitVersionDetails = gitVersionInfo()

                println(gitVersionDetails.version)
                println(gitVersionDetails.versionCode)
                println(gitVersionDetails.branchName)
                println(gitVersionDetails.gitHashFull)
                println(gitVersionDetails.gitHash)
                println(gitVersionDetails.lastTag)
                println(gitVersionDetails.isCleanTag)
                println(gitVersionDetails.commitDistance)
            }
        }
    }
}
