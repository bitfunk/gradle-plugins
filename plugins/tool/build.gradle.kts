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

plugins {
    id("eu.bitfunk.gradle.plugin.development.convention")
}

group = "eu.bitfunk.gradle.plugin.tool"

dependencies {
    implementation("eu.bitfunk.gradle.plugin.tool:tool-version")
}

// Delegate to included builds
tasks.maybeCreate("build").dependsOn(gradle.includedBuilds.map { it.task(":build") })
tasks.maybeCreate("check").dependsOn(gradle.includedBuilds.map { it.task(":check") })
tasks.maybeCreate("clean").dependsOn(gradle.includedBuilds.map { it.task(":clean") })
tasks.maybeCreate("jacocoTestReport").dependsOn(gradle.includedBuilds.map { it.task(":jacocoTestReport") })
tasks.maybeCreate("wrapper").dependsOn(gradle.includedBuilds.map { it.task(":wrapper") })
tasks.maybeCreate("dependencyUpdates").dependsOn(gradle.includedBuilds.map { it.task(":dependencyUpdates") })
tasks.maybeCreate("versionCatalogUpdate").dependsOn(gradle.includedBuilds.map { it.task(":versionCatalogUpdate") })
