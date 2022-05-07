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
    id("eu.bitfunk.gradle.plugin.version.catalog")
}

group = "eu.bitfunk.gradle.plugin.quality"

gradlePlugin {
    plugins.create("qualityCodeAnalysis") {
        id = "eu.bitfunk.gradle.plugin.quality.code.analysis"
        implementationClass = "eu.bitfunk.gradle.plugin.quality.code.analysis.CodeAnalysisPlugin"
    }
}

dependencies {
    implementation(libs.gradleDetektPlugin)
}

versionCatalogAccessor {
    packageName.set("eu.bitfunk.gradle.plugin.quality.code.analysis.libs")
}

apiValidation {
    ignoredPackages.add("eu.bitfunk.gradle.plugin.quality.code.analysis.libs")
}
