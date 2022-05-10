# Versioning Plugin

A plugin to generate semantic version information and apply it to the project based on git tags. It makes use of the [gradle git plugin](https://github.com/bitfunk/gradle-git-version).

## Features

As this plugin wraps git-version, it offers the same features for versioning. 

Additionally it adds:
- versionCode - version code for Android based on semantic versioning (MMmmPP)
- featureVersionCode - version code for Android based on timesstamps (MMddHHmm)
- versionInfo - prints all information about the current version

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
    id("eu.bitfunk.gradle.plugin.tool.versioning")
}
```

## Usage

To print current version information:

```bash
./gradlew versionInfo
```
