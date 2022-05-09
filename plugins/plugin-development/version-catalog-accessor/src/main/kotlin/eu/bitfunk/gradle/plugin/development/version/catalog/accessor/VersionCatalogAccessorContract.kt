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

package eu.bitfunk.gradle.plugin.development.version.catalog.accessor

import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

public interface VersionCatalogAccessorContract {

    public interface Plugin {
        public fun checkPreconditions(project: Project)

        public fun addExtension(project: Project): Extension

        public fun addSourceGeneratorTask(
            project: Project,
            extension: Extension
        ): VersionCatalogAccessorSourceGeneratorTask

        public fun addGeneratorTask(project: Project)

        public fun configureSourceSet(project: Project)

        public fun configureCodeCoverage(project: Project)
    }

    public interface Extension {
        /**
         * Folder the version catalog files are stored in. Relative to project root.
         *
         * Default: gradle/
         */
        public val catalogSourceFolder: Property<String>

        /**
         * Package name the generated VersionCatalogAccessor is added to.
         *
         * Default: empty
         */
        public val packageName: Property<String>

        /**
         * Names of the catalog file a VersionCatalogAccessor should be generated for.
         *
         * Default: ["libs"]
         */
        public val catalogNames: ListProperty<String>
    }

    public interface Task {
        public interface Generator {
            public fun generate()
        }
    }

    public companion object {
        public const val EXTENSION_NAME: String = "versionCatalogAccessor"
        public const val TASK_NAME_GENERATE: String = "generateVersionCatalogAccessor"
        public const val TASK_NAME_GENERATE_SOURCE: String = "generateVersionCatalogAccessorSource"
    }
}
