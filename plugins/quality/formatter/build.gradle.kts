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

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libsFormatter.plugins.gradlePluginConvention)
}

group = "eu.bitfunk.gradle.plugin.quality"
version = "0.1.1"

gradlePlugin {
    plugins.create("qualityFormatter") {
        id = "eu.bitfunk.gradle.plugin.quality.formatter"
        implementationClass = "eu.bitfunk.gradle.plugin.quality.formatter.FormatterPlugin"
    }
}

dependencies {
    implementation(libsFormatter.gradleSpotlessPlugin)
    implementation(libsFormatter.gradleKtlintPlugin)
}

projectConfig {
    publishName.set("Formatter")
    publishDescription.set("A source code formatter plugin applying bitfunk code style.")
    publishGitHubOrganization.set("bitfunk")
    publishGitHubRepositoryName.set("gradle-plugins")
}
