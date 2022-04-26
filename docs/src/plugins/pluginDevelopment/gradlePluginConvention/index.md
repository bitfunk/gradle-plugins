# Gradle Plugin Convention

The plugin applies configuration for Gradle plugin development to ease configuration effort. It sets Java 11 support, default dependencies, tests and coverage settings. It also enables [explicit API mode for Kotlin](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors).

## Requirements

* Gradle 7.2+
* Can only be applied to Gradle plugins with the java-gradle-plugin available.

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
    id("eu.bitfunk.gradle.plugin.convention")
}
```
