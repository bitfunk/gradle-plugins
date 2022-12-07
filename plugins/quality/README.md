# Quality Plugin

[![QualityCollection](../../docs/assets/images/badge-release-quality-collection.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.quality)

This plugin is a collection of all quality plugins.

## Features

Quick access to all quality plugins in one:

- [Code Analysis](../../docs/src/plugins/quality/code-analysis/index.md) - source code analysis
- [Formatter](formatter/README.md) - source code formatter applying bitfunk code style
- [Report](report/README.md) - quality report plugin using sonarqube

## Installation

Add the following to your project root `build.gradle/build.gradle.kts` file:

```kotlin
plugins {
    id("eu.bitfunk.gradle.plugin.quality")
}
```

## Usage

Read the corresponding quality plugin documentation listed under features.
