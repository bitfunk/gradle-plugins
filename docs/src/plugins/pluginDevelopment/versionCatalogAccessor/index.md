# VersionCatalogAccessor

This plugin generates an accessor to expose the [version catalog (toml)](https://docs.gradle.org/current/userguide/platforms.html) content to Gradle plugins. This allows plugins to reuse an already available version catalog in it's own implementation similarly to typesafe accessors.

## Requirements

* Gradle 7.2+
* Can only be applied to Gradle plugins with the java-gradle-plugin available.
* A version catalog available and enabled (default: `libs.versions.toml`)

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

plugins {
    id("eu.bitfunk.gradle.version.catalog")
}
```

The plugin works with a default version catalog setup, but could be configured to your needs:

* **_catalogSourceFolder_**: relative path to the version catalog toml files. Default: `gradle/`
* **_catalogNames_**: names of the catalogs for whom to create an accessor. Default: `listOf("libs")`
* **_packageName_**: package used for the generated accessor. Default: empty

```kotlin
versionCatalogHelper {
    catalogSourceFolder.set("")
    catalogNames.set(listOf("libs", "deps"))
    packageName.set("com.example")
}
```

## Usage

To generate the version catalog accessor

```bash
./gradlew generateVersionCatalogAccessor
```
