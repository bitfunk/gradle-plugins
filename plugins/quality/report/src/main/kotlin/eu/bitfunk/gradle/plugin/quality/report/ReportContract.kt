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

package eu.bitfunk.gradle.plugin.quality.report

import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File

public interface ReportContract {
    public interface Plugin {
        public fun addPlugins(project: Project)

        public fun addExtension(project: Project): Extension

        public fun configureReport(
            project: Project,
            extension: Extension,
            collector: Collector
        )

        public fun configureTasks(project: Project, extension: Extension)
    }

    public interface Extension {
        public val sonarProjectKey: Property<String>
        public val sonarOrganization: Property<String>
        public val coverageReportSourceDir: Property<String>
    }

    public interface Collector {
        public fun collectProjects(sourcePath: File, filterPath: String): List<String>
    }

    public companion object {
        public const val EXTENSION_NAME: String = "reportConfig"
    }
}
