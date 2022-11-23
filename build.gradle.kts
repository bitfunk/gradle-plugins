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
    alias(libs.plugins.gradleQuality)
    alias(libs.plugins.gradleVersioning)
    alias(libs.plugins.gradleCompositeDelegator)
}

reportConfig {
    sonarProjectKey.set("bitfunk_gradle-plugins")
    sonarOrganization.set("bitfunk")
    coverageReportSourceDirs.set(
        listOf(
            "$projectDir/plugin-development/build/reports/jacoco/testCodeCoverageReport",
            "$projectDir/plugins/build/reports/jacoco/testCodeCoverageReport"
        )
    )
}

project(":docs") {
    sonarqube {
        isSkipProject = true
    }
}

compositeDelegator {
    additionalTasks.set(listOf("testCodeCoverageReport"))
}

tasks.maybeCreate("clean", Delete::class.java).delete("build")

tasks.named<Wrapper>("wrapper") {
    gradleVersion = libs.versions.gradle.get()
    distributionType = Wrapper.DistributionType.ALL
}
