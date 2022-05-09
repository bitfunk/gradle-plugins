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

import eu.bitfunk.gradle.plugin.tool.version.VersionContract.Generator
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

public class VersionPlugin() : Plugin<Project>, VersionContract.Plugin {

    public constructor(generator: Generator) : this() {
        _generator = generator
    }

    private var _generator: Generator? = null

    private fun getGenerator(project: Project): Generator {
        return if (_generator == null)
            VersionNameGenerator(project)
        else {
            _generator as Generator
        }
    }

    override fun apply(target: Project) {
        val generator = getGenerator(target)

        addPlugins(target)
        configureVersion(target, generator)
        configureVersionTasks(target, generator)
    }

    override fun addPlugins(project: Project): Unit = with(project) {
        pluginManager.apply("eu.upwolf.git-version")
    }

    override fun configureVersion(project: Project, generator: Generator): Unit = with(project) {
        allprojects {
            version = generator.generateVersionName()
            extra.set("versionCode", generator.generateVersionCode())
            extra.set("versionCodeFeature", generator.generateFeatureVersionCode())
        }
    }

    override fun configureVersionTasks(project: Project, generator: Generator): Unit = with(project) {
        tasks.register("versionInfo") {
            group = "versioning"

            doLast {
                println("VersionName: ${generator.generateVersionName()}")
                println("VersionCode: ${generator.generateVersionCode()}")
                println("VersionCodeFeature: ${generator.generateFeatureVersionCode()}")
                println("VersionDetails: ${generator.generateVersionDetails()}")
            }
        }
    }
}
