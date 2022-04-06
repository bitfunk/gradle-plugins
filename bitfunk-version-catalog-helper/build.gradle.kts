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
    `kotlin-dsl`
    `java-gradle-plugin`
    jacoco

    alias(libs.plugins.binaryCompatibilityValidator)
}

group = "eu.bitfunk.gradle.version.catalog"
version = "0.1.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}

gradlePlugin {
    val versionCatalog by plugins.creating {
        id = "eu.bitfunk.gradle.version.catalog"
        implementationClass = "eu.bitfunk.gradle.version.catalog.VersionCatalogHelperPlugin"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    explicitApi()
}

dependencies {
    implementation("com.squareup:kotlinpoet:1.10.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.13.0")

    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("io.mockk:mockk:1.12.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

apiValidation {
    ignoredPackages.add("eu.bitfunk.gradle.version.catalog.helper")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.register<Copy>("copySources") {
    from("src/main/kotlin/eu/bitfunk/gradle/version/catalog/helper")
    into("src/main/resources/sources")
}

tasks.named("assemble") {
    dependsOn("copySources")
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "7.4"
    distributionType = Wrapper.DistributionType.ALL
}
