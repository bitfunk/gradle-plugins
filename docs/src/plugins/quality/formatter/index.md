# Formatter Plugin

The plugin helps to format source code of your project and warns about issues. It makes use of the [spotless plugin](https://github.com/diffplug/spotless) and applies bitfunk flavoured default configuration.

## Features

As this plugin wraps spotless, it offers the same features with bitfunk project configuration. For kotlin it's using [ktlint](https://github.com/pinterest/ktlint) and Markdown is using [prettier](https://prettier.io/).

Supported source files:

- Kotlin (.kt)
- Kotlin Gradle (.kts)
- Markdown (.md)

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
    id("eu.bitfunk.gradle.plugin.quality.formatter")
}
```

## Usage

To check source code formatting

```bash
./gradlew spotlessCheck
```

For applying source code formatting

```bash
./gradlew spotlessApply
```
