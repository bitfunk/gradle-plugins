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

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke

public fun Project.gitVersion(): String {
    @Suppress("UNCHECKED_CAST")
    val loadGitVersion = extensions.extraProperties.get("gitVersion") as Closure<String>
    return loadGitVersion()
}

public fun Project.gitVersionInfo(): GitVersionInfo {
    @Suppress("UNCHECKED_CAST")
    val loadGitVersionInfo = extensions.extraProperties.get("gitVersionInfo") as Closure<GitVersionInfo>
    return loadGitVersionInfo()
}
