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
    id("eu.bitfunk.gradle.plugin.quality.formatter")
    id("eu.bitfunk.gradle.plugin.quality.code.analysis")
    id("eu.bitfunk.gradle.plugin.tool.versioning")

    id("org.sonarqube") version "3.3"
}

sonarqube {
    properties {
        property("sonar.projectKey", "bitfunk_gradle-plugins")
        property("sonar.organization", "bitfunk")
        property("sonar.host.url", "https://sonarcloud.io")

        property("sonar.sources", collectProjects(projectDir, "src/main/kotlin").map { "$it/src/main/kotlin" })
        property("sonar.tests", collectProjects(projectDir, "src/test/kotlin").map { "$it/src/test/kotlin" })
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.jacoco.reportPaths", "$buildDir/reports/jacoco/testCodeCoverageReport.xml")
    }
}

tasks.create("testDirs") {
    doLast {
        println(collectProjects(projectDir, "src/main/kotlin"))
    }
}

fun collectProjects(file: File, filter: String): List<File> {
    val projects = mutableListOf<File>()

    listOf(file)
        .extract(projects, filter)
        .extract(projects, filter)
        .extract(projects, filter)
        .toList()

    return projects
}

fun List<File>.extract(targetList: MutableList<File>, filter: String): List<File> {
    return flatMap { it.listFiles().asSequence() }
        .filter { it.isDirectory && File(it, filter).exists() }
        .map { it.also { targetList.add(it) } }
}


tasks.named("sonarqube") {
    dependsOn("copyCoverageReports")
}

project(":docs") {
    sonarqube {
        isSkipProject = true
    }
}

tasks.create<Copy>("copyCoverageReports") {
    dependsOn("testCodeCoverageReport")

    group = "verification"

    from("$projectDir/plugins/build/reports/jacoco/testCodeCoverageReport") {
        include("*.xml")
    }

    into("$buildDir/reports/jacoco")

    includeEmptyDirs = false
}

tasks.maybeCreate("clean", Delete::class.java).delete("build")

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "7.4.2"
    distributionType = Wrapper.DistributionType.ALL
}

// Delegate to included builds
tasks.maybeCreate("build").dependsOn(gradle.includedBuilds.map { it.task(":build") })
tasks.maybeCreate("check").dependsOn(gradle.includedBuilds.map { it.task(":check") })
tasks.maybeCreate("clean").dependsOn(gradle.includedBuilds.map { it.task(":clean") })
tasks.maybeCreate("jacocoTestReport").dependsOn(gradle.includedBuilds.map { it.task(":jacocoTestReport") })
tasks.maybeCreate("testCodeCoverageReport").dependsOn(gradle.includedBuilds.map { it.task(":testCodeCoverageReport") })
tasks.maybeCreate("wrapper").dependsOn(gradle.includedBuilds.map { it.task(":wrapper") })
tasks.maybeCreate("dependencyUpdates").dependsOn(gradle.includedBuilds.map { it.task(":dependencyUpdates") })
tasks.maybeCreate("versionCatalogUpdate").dependsOn(gradle.includedBuilds.map { it.task(":versionCatalogUpdate") })
