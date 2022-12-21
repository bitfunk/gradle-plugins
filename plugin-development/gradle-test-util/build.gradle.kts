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

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libsGradleTestUtil.plugins.kotlin.jvm)
    `kotlin-dsl`
    jacoco
    alias(libsGradleTestUtil.plugins.kotlin.binaryCompatibilityValidator)
    alias(libsGradleTestUtil.plugins.mavenPublishPlugin)
    id("jacoco-report-aggregation")
}

group = "eu.bitfunk.gradle.plugin.development.test"
version = "0.1.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libsGradleTestUtil.plugin.kotlin.gradleDsl)
    implementation(libsGradleTestUtil.test.mockk)

    testImplementation(libsGradleTestUtil.test.jUnit5)
    testRuntimeOnly(libsGradleTestUtil.test.jUnit5.engine)
    testImplementation(libsGradleTestUtil.test.mockk)
}

mavenPublishing {
    group = requireNotNull(project.group)
    version = requireNotNull(project.version)

    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()
    configure(
        KotlinJvm(
            javadocJar = JavadocJar.Javadoc(),
            sourcesJar = true
        )
    )
    pom {
        name.set("Test utils for Gradle plugin development")
        description.set("A Collection of test utils to ease Gradle plugin testing.")
        inceptionYear.set("2022")
        url.set("https://github.com/bitfunk/gradle-plugins/")
        licenses {
            license {
                name.set("ISC License")
                url.set("https://opensource.org/licenses/isc")
                distribution.set("https://github.com/bitfunk/gradle-plugins/blob/main/LICENSE")
            }
        }
        developers {
            developer {
                id.set("bitfunk")
                name.set("Wolf-Martell Montwé (bitfunk)")
                url.set("https://github.com/bitfunk/")
            }
        }
        scm {
            url.set("https://github.com/bitfunk/gradle-plugins/")
            connection.set("scm:git:git://github.com/bitfunk/gradle-plugins.git")
            developerConnection.set("scm:git:ssh://github.com/bitfunk/gradle-plugins.git")
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("test"))

    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn(tasks.named("jacocoTestReport"))

    violationRules {
        rule {
            limit {
                minimum = BigDecimal(0.95)
            }
        }
    }
}

tasks.named("check") {
    dependsOn(tasks.named("jacocoTestCoverageVerification"))
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = libsGradleTestUtil.versions.gradle.get()
    distributionType = Wrapper.DistributionType.ALL
}
