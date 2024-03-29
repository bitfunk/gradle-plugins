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

package eu.bitfunk.gradle.plugin.quality.formatter

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories

public class FormatterPlugin : Plugin<Project>, FormatterContract.Plugin {

    override fun apply(target: Project) {
        addPlugins(target)
        addRepository(target)
        configureFormatter(target)
    }

    override fun addPlugins(project: Project): Unit = with(project) {
        pluginManager.apply("com.diffplug.spotless")
    }

    override fun addRepository(project: Project): Unit = with(project) {
        repositories {
            mavenCentral()
        }
    }

    override fun configureFormatter(project: Project): Unit = with(project) {
        spotless {
            kotlin {
                ktlint()
                target("**/*.kt")
                targetExclude("**/build/", "**/resources/")
            }
            kotlinGradle {
                ktlint()
                target("**/*.gradle.kts", "**/*.df.kts")
                targetExclude("**/build/")
            }
            format("markdown") {
                prettier()
                target("**/*.md")
            }
            format("misc") {
                target("**/.gitignore", "**/.gitattributes", ".java-version")
                trimTrailingWhitespace()
                indentWithSpaces()
                endWithNewline()
            }
        }
    }

    private fun Project.spotless(action: Action<SpotlessExtension>) {
        extensions.configure(SpotlessExtension::class.java, action)
    }
}
