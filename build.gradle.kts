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

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven {
            url = uri("https://maven.pkg.github.com/bitfunk/gradle-git-version")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_PACKAGE_DOWNLOAD_USER")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_PACKAGE_DOWNLOAD_KEY")
            }
        }
    }
}

plugins {
    id("eu.bitfunk.gradle.plugin.quality")
    id("eu.bitfunk.gradle.plugin.tool.versioning")
    id("eu.bitfunk.gradle.plugin.tool.composite.delegator")
}

reportConfig {
    sonarProjectKey.set("bitfunk_gradle-plugins")
    sonarOrganization.set("bitfunk")
    coverageReportSourceDir.set("$projectDir/plugins/build/reports/jacoco/testCodeCoverageReport")
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
    gradleVersion = "7.4.2"
    distributionType = Wrapper.DistributionType.ALL
}
