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
    alias(libs.plugins.mkdocs)
}

python {
    pip(
        "mkdocs-include-markdown-plugin:3.3.0",
        "mkdocs-kroki-plugin:0.3.0",
        "mkdocs-markdownextradata-plugin:0.2.5",
        "mkdocs-material:8.2.1",
        "mkdocs-minify-plugin:0.5.0",
        "mkdocs-redirects:1.0.3",
        "pygments:2.11.2",
        "pymdown-extensions:9.2"
    )
}

val currentDocVersion = if (project.hasProperty("release")) "0.1.0" else "snapshot"

mkdocs {
    sourcesDir = "./"

    publish.docPath = currentDocVersion
    publish.rootRedirect = project.hasProperty("release")
    if (project.hasProperty("release")) {
        publish.rootRedirectTo = "latest"
        publish.setVersionAliases("latest")
    }
    publish.generateVersionsFile = true

    strict = true

    extras = mapOf(
        "version" to currentDocVersion
    )
}

tasks.register<Exec>("gitDisableSigning") {
    group = "publishing"

    workingDir(".gradle/gh-pages")

    commandLine("git config commit.gpgsign false".split(" "))
}

tasks.named("gitPublishReset") {
    dependsOn("gitDisableSigning")
}

tasks.register<Delete>("clean") {
    delete("build")
}
