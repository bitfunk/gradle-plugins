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
        "mkdocs:1.3.1",
        "mkdocs-include-markdown-plugin:3.8.1",
        "mkdocs-kroki-plugin:0.3.0",
        "mkdocs-markdownextradata-plugin:0.2.5",
        "mkdocs-material:8.5.3",
        "mkdocs-minify-plugin:0.5.0",
        "mkdocs-redirects:1.2.0",
        "mkdocs-minify-plugin:0.5.0",
        "pygments:2.13.0",
        "pymdown-extensions:9.5"
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

tasks.register<Delete>("clean") {
    delete("build")
}
