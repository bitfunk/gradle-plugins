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

package eu.bitfunk.gradle.plugin.development.convention

import org.gradle.api.Project
import org.gradle.api.provider.Property

public interface GradlePluginConventionContract {

    public interface Plugin {
        public fun checkPreconditions(project: Project)
        public fun addPlugins(project: Project)
        public fun addRepositories(project: Project)
        public fun addExtension(project: Project): Extension
        public fun configureJavaCompatibility(project: Project)
        public fun configureKotlin(project: Project)
        public fun configureDependencies(project: Project)
        public fun configureTests(project: Project)
        public fun configureTestCoverage(project: Project)
        public fun configureTestCoverageTasks(project: Project)
        public fun configureGradleWrapper(project: Project)
    }

    public interface Extension {
        /**
         * Publishing name
         */
        public val publishName: Property<String>

        /**
         * Publishing description
         */
        public val publishDescription: Property<String>

        /**
         * Publishing GitHub organization
         */
        public val publishGitHubOrganization: Property<String>

        /**
         * Publishing GitHub repository name
         */
        public val publishGitHubRepositoryName: Property<String>
    }

    public companion object {
        public const val EXTENSION_NAME: String = "projectConfig"
    }
}
