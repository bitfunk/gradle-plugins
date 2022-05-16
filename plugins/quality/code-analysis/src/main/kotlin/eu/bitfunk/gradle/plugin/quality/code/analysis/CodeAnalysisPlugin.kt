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

package eu.bitfunk.gradle.plugin.quality.code.analysis

import eu.bitfunk.gradle.plugin.quality.code.analysis.libs.generated.LibsCodeAnalysisVersionCatalogAccessor
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType

public class CodeAnalysisPlugin : Plugin<Project>, CodeAnalysisContract.Plugin {

    override fun apply(target: Project) {
        addPlugins(target)
        configureAnalysis(target)
        configureAnalysisTasks(target)
    }

    override fun addPlugins(project: Project): Unit = with(project) {
        pluginManager.apply("io.gitlab.arturbosch.detekt")
    }

    override fun configureAnalysis(project: Project): Unit = with(project) {
        val libs = LibsCodeAnalysisVersionCatalogAccessor(project)

        detekt {
            toolVersion = libs.versions.detekt.get()
            parallel = true

            source = project.files(
                project.file(project.rootDir)
            )

            config = project.rootProject.files("config/detekt/config.xml")
            baseline = project.rootProject.file("config/detekt/baseline.yml")
        }
    }

    override fun configureAnalysisTasks(project: Project): Unit = with(project) {
        tasks.withType<Detekt>().configureEach {
            jvmTarget = "11"

            exclude(
                "**/.gradle/**",
                "**/.idea/**",
                "**/build/**",
                ".github/**",
                "gradle/**",
            )
            reports {
                xml.required.set(true)
                html.required.set(true)
            }
        }

        tasks.withType<DetektCreateBaselineTask>().configureEach {
            exclude(
                "**/.gradle/**",
                "**/.idea/**",
                "**/build/**",
                "**/gradle/wrapper/**",
                ".github/**",
                "assets/**",
                "app-ios/**",
                "docs/**",
                "gradle/**",
                "**/*.adoc",
                "**/gradlew",
                "**/LICENSE",
                "**/.java-version",
                "**/gradlew.bat",
                "**/*.png",
                "**/*.properties",
                "**/*.pro",
                "**/*.sq",
                "**/*.xml",
                "**/*.yml",
            )
        }
    }

    private fun Project.detekt(action: Action<DetektExtension>) {
        extensions.configure(DetektExtension::class.java, action)
    }
}
