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

package eu.bitfunk.gradle.plugin.tool.publish

import org.gradle.api.Project
import org.gradle.api.provider.Property

public interface PublishContract {

    public interface Plugin {
        public fun addPlugins(project: Project)
        public fun addExtension(project: Project): Extension

        /**
         * Configures according to https://central.sonatype.org/publish/requirements/
         */
        public fun configurePublishing(project: Project, extension: Extension)
        public fun configureSigning(project: Project, extension: Extension)
    }

    public interface Extension {
        public val projectName: Property<String>
        public val projectDescription: Property<String>
        public val projectUrl: Property<String>

        public val licenseName: Property<String>
        public val licenseUrl: Property<String>

        public val developerName: Property<String>
        public val developerEmail: Property<String>

        public val organizationName: Property<String>
        public val organizationUrl: Property<String>

        /**
         * Url to the main branch of your project.
         *
         * Example: https://github.com/bitfunk/gradle-plugins/tree/main
         */
        public val scmUrl: Property<String>

        public val signingEnabled: Property<Boolean>
    }

    public companion object {
        public const val EXTENSION_NAME: String = "publishConfig"
    }
}
