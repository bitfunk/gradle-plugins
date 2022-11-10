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

import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    jacoco
    alias(libsPluginConvention.plugins.binaryCompatibilityValidator)
    alias(libsPluginConvention.plugins.gradleMavenPublishPlugin)
}

group = "eu.bitfunk.gradle.plugin.development.convention"
version = "0.0.1-SNAPSHOT"

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    google()
}

gradlePlugin {
    plugins.create("gradlePluginConvention") {
        id = "eu.bitfunk.gradle.plugin.development.convention"
        implementationClass = "eu.bitfunk.gradle.plugin.development.convention.GradlePluginConventionPlugin"
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
    implementation(libsPluginConvention.gradleKotlinPlugin)
    implementation(libsPluginConvention.gradleKotlinDsl)
    implementation(libsPluginConvention.gradleKotlinBinaryCompatibilityPlugin)

    testImplementation(gradleTestKit())
    testImplementation(libsPluginConvention.testJUnit5)
    testRuntimeOnly(libsPluginConvention.testJUnit5Engine)
    testImplementation(libsPluginConvention.testMockk)
    testImplementation(libsPluginConvention.testGradleTestUtil)
}

mavenPublishing {
    group = requireNotNull(project.group)
    version = requireNotNull(project.version)

    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()
    configure(
        GradlePlugin(
            javadocJar = JavadocJar.Javadoc(),
            sourcesJar = true
        )
    )
    pom {
        name.set("Gradle plugin development convention")
        description.set("A Collection of Gradle plugins to simplify and unify project development.")
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
    gradleVersion = libsPluginConvention.versions.gradle.get()
    distributionType = Wrapper.DistributionType.ALL
}
