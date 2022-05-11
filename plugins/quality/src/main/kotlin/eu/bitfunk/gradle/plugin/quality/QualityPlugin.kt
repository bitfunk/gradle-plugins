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

package eu.bitfunk.gradle.plugin.quality

import org.gradle.api.Plugin
import org.gradle.api.Project

public class QualityPlugin : QualityContract.Plugin, Plugin<Project> {
    override fun apply(target: Project) {
        addPlugins(target)
    }

    override fun addPlugins(project: Project): Unit = with(project) {
        pluginManager.apply("eu.bitfunk.gradle.plugin.quality.code.analysis")
        pluginManager.apply("eu.bitfunk.gradle.plugin.quality.formatter")
        pluginManager.apply("eu.bitfunk.gradle.plugin.quality.report")
    }
}
