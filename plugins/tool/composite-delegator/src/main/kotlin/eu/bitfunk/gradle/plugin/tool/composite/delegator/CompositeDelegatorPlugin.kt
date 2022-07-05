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

package eu.bitfunk.gradle.plugin.tool.composite.delegator

import eu.bitfunk.gradle.plugin.tool.composite.delegator.CompositeDelegatorContract.Companion.EXTENSION_NAME
import eu.bitfunk.gradle.plugin.tool.composite.delegator.CompositeDelegatorContract.Extension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.testing.Test
import kotlin.collections.Map.Entry

public class CompositeDelegatorPlugin : CompositeDelegatorContract.Plugin, Plugin<Project> {

    override fun apply(target: Project) {
        val extension = addExtension(target)
        configureTasks(target, extension)
    }

    override fun addExtension(project: Project): Extension = with(project) {
        val extension = extensions.create(
            EXTENSION_NAME,
            CompositeDelegatorPluginExtension::class.java
        )

        extension.additionalTasks.convention(emptyList())

        return extension
    }

    override fun configureTasks(project: Project, extension: Extension): Unit = with(project) {
        DEFAULT_TASKS.map { addTask(project, it) }

        afterEvaluate {
            extension.additionalTasks.get().associateWith { null }.map { addTask(project, it) }
        }
    }

    private fun addTask(project: Project, item: Entry<String, Class<out ConventionTask>?>): Unit = with(project) {
        val task = if (item.value != null) {
            tasks.maybeCreate(item.key, item.value!!)
        } else {
            tasks.maybeCreate(item.key)
        }
        task.dependsOn(gradle.includedBuilds.map { it.task(":${item.key}") })
    }

    private companion object {
        private val DEFAULT_TASKS = mapOf(
            "assemble" to null,
            "build" to null,
            "check" to null,
            "clean" to Delete::class.java,
            "jacocoTestReport" to null,
            "test" to Test::class.java,
            "wrapper" to null,
            "dependencyUpdates" to null,
            "versionCatalogUpdate" to null,
        )
    }
}
