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

package eu.bitfunk.gradle.plugin.convention

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.math.BigDecimal

public class GradlePluginConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.repositories {
            gradlePluginPortal()
            mavenCentral()
            google()
        }

        target.addPlugins()

        target.configureJavaCompatibility()
        target.configureKotlin()
        target.configureDependencies()
        target.configureTests()
        target.configureCoverage()
        target.configureWrapper()
    }

    private fun Project.addPlugins() {
        pluginManager.apply("org.gradle.java-gradle-plugin")
        pluginManager.apply("org.gradle.kotlin.kotlin-dsl")
        pluginManager.apply("org.gradle.jacoco")
        pluginManager.apply("org.jetbrains.kotlinx.binary-compatibility-validator")
    }

    private fun Project.configureJavaCompatibility() {
        javaPlugin {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    private fun Project.configureKotlin() {
        kotlinJvm {
            explicitApi()
        }
    }

    private fun Project.configureDependencies() {
        dependencies {
            testImplementation(gradleTestKit())
            testImplementation("org.junit.jupiter:junit-jupiter:$JUNIT_5_VERSION")
            testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$JUNIT_5_VERSION")
            testImplementation("io.mockk:mockk:$MOCKK_VERSION")
        }
    }

    private fun Project.configureTests() {
        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }

    private fun Project.configureCoverage() {
        jacoco {
            version = JACOCO_VERSION
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
    }

    private fun Project.configureWrapper() {
        tasks.named<Wrapper>("wrapper") {
            gradleVersion = GRADLE_VERSION
            distributionType = Wrapper.DistributionType.ALL
        }
    }

    private companion object {
        const val PLUGIN_BINARY_VALIDATOR_VERSION = "0.8.0"
        const val GRADLE_VERSION = "7.4.2"
        const val JUNIT_5_VERSION = "5.8.2"
        const val MOCKK_VERSION = "1.12.2"
        const val JACOCO_VERSION = "0.8.7"
    }
}


