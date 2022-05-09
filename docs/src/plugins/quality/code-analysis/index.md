# Code Analysis Plugin

The plugin helps to analyse code of your project and warns about issues. It makes use of the [detekt plugin](https://github.com/detekt/detekts).

## Features

As this plugin wraps detekt, it offers the same features for project configuration.

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
    id("eu.bitfunk.gradle.plugin.quality.code.analysis")
}
```

## Usage

To check source code:

```bash
./gradlew detekt
```

For applying initial configuration

```bash
./gradlew detektGenerateConfig
```

For setting a baseline

```bash
./gradlew detektBaseline
```
