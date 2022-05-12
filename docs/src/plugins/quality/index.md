# Quality Plugin

The plugin is a collection of all quality plugins.

## Features

Quick access to all quality plugins in one:

- [Code Analysis](./code-analysis/index.md) - a source code analysis
- [Formatter](./formatter/index.md) - a source code formatter applying bitfunk code style
- [Report](./report/index.md) - a quality report plugin using sonarqube

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
    id("eu.bitfunk.gradle.plugin.quality")
}
```

## Usage

Read the corresponding quality plugin documentation.
