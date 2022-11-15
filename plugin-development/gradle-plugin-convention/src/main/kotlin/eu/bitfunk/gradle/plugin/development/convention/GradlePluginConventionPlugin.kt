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

package eu.bitfunk.gradle.plugin.development.convention

import eu.bitfunk.gradle.plugin.development.convention.GradlePluginConventionContract.Extension
import eu.bitfunk.gradle.plugin.development.convention.internal.configurePublishing
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.util.GradleVersion
import java.math.BigDecimal

public class GradlePluginConventionPlugin : Plugin<Project>, GradlePluginConventionContract.Plugin {

    override fun apply(target: Project) {
        checkPreconditions(target)
        addPlugins(target)
        addRepositories(target)
        val extension = addExtension(target)
        configureJavaCompatibility(target)
        configureKotlin(target)
        configureDependencies(target)
        configurePublishing(target, extension)
        configureTests(target)
        configureTestCoverage(target)
        configureTestCoverageTasks(target)
        configureGradleWrapper(target)
    }

    override fun checkPreconditions(project: Project) {
        if (GradleVersion.current() < GradleVersion.version("7.2")) {
            throw GradleException("This plugin requires Gradle 7.2 or later")
        }
    }

    public override fun addPlugins(project: Project): Unit = with(project) {
        pluginManager.apply("org.gradle.java-gradle-plugin")
        pluginManager.apply("org.gradle.kotlin.kotlin-dsl")
        pluginManager.apply("org.gradle.jacoco")
        pluginManager.apply("org.jetbrains.kotlinx.binary-compatibility-validator")
    }

    public override fun addRepositories(project: Project): Unit = with(project) {
        repositories {
            gradlePluginPortal()
            mavenCentral()
            google()
            maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
    }

    override fun addExtension(project: Project): Extension = with(project) {
        val extension = extensions.create(
            GradlePluginConventionContract.EXTENSION_NAME,
            GradlePluginConventionPluginExtension::class.java
        )

        extension.publishName.convention("")
        extension.publishDescription.convention("")
        extension.publishGitHubOrganization.convention("")
        extension.publishGitHubRepositoryName.convention("")

        return extension
    }

    public override fun configureJavaCompatibility(project: Project): Unit = with(project) {
        javaPlugin {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    public override fun configureKotlin(project: Project): Unit = with(project) {
        kotlinJvm {
            explicitApi()
        }
    }

    public override fun configureDependencies(project: Project): Unit = with(project) {
        dependencies {
            testImplementation(gradleTestKit())
            testImplementation("org.junit.jupiter:junit-jupiter:$JUNIT_5_VERSION")
            testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$JUNIT_5_VERSION")
            testImplementation("io.mockk:mockk:$MOCKK_VERSION")
            testImplementation("eu.bitfunk.gradle.plugin.development.test:gradle-test-util:$GRADLE_TEST_UTIL")
        }
    }

    public override fun configureTests(project: Project): Unit = with(project) {
        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }

    public override fun configureTestCoverage(project: Project): Unit = with(project) {
        jacoco {
            toolVersion = JACOCO_VERSION
        }
    }

    override fun configureTestCoverageTasks(project: Project): Unit = with(project) {
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
                        minimum = BigDecimal(COVERAGE_MINIMUM)
                    }
                }
            }
        }

        tasks.named("check") {
            dependsOn(tasks.named("jacocoTestCoverageVerification"))
        }
    }

    public override fun configureGradleWrapper(project: Project): Unit = with(project) {
        tasks.named<Wrapper>("wrapper") {
            gradleVersion = GRADLE_VERSION
            distributionType = Wrapper.DistributionType.ALL
        }
    }

    private companion object {
        const val GRADLE_VERSION = "7.5.1"
        const val JUNIT_5_VERSION = "5.8.2"
        const val MOCKK_VERSION = "1.12.8"
        const val JACOCO_VERSION = "0.8.8"
        const val GRADLE_TEST_UTIL = "0.1.0"

        const val COVERAGE_MINIMUM = 0.95
    }
}
