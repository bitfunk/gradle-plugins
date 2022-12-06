# Versioning Plugin

[![ToolVersioning](../../../docs/assets/images/badge-release-tool-versioning.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.tool.versioning)

A plugin to generate semantic version information and apply it to the project based on git tags. It makes use of the [git-version plugin](https://github.com/bitfunk/gradle-plugins).

## Features

As this plugin wraps git-version, it offers the same features for versioning.

Additionally it adds:

- versionCode - version code for Android based on semantic versioning (MMmmPP)
- featureVersionCode - version code for Android based on timestamps (MMddHHmm)
- versionInfo - prints all information about the current version

## Installation

Add the following to your project module `build.gradle/build.gradle.kts` file:

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

To access the version information, use this project extensions:

- `version()` - version as String
- `versionCleaned` - version where `-SNAPSHOT` got removed
- `versionCode()` - versionCode as Int
- `versionCodeFeature()` - versionCodeFeature as Int
