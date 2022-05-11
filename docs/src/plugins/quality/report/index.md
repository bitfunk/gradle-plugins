# Report Plugin

The plugin configures sonarqube scanner to report code metrics to [sonarcloud.io](https://sonarcloud.io). It makes use of the [SonarScanner for Gradle](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-gradle/) and applies default configuration.

## Features

This plugin wraps the sonar scanner Gradle plugin and applies some configuration. It also collects project sourceFiles for the report. This works for subprojects and included builds. It also copies code coverage reports to the right location for the scanner plugin.

## Installation

Add to your project root `build.gradle/build.gradle.kts` file to download from GitHub packages:

```kotlin
buildscript {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/bitfunk/gradle-plugins")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("PACKAGE_REGISTRY_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("PACKAGE_REGISTRY_TOKEN")
            }
        }
    }
}
```

and

```kotlin
plugins {
    id("eu.bitfunk.gradle.plugin.quality.report")
}
```

## Usage

To run the report generation and upload to sonarcloud.io:

```bash
./gradlew sonarqube
```

You need to provide a `SONAR_TOKEN` from [sonarcloud.io](https://sonarcloud.io) to be able to upload reports.
