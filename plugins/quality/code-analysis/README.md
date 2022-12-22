# Code Analysis Plugin

[![QualityCodeAnalysis](../../../docs/assets/images/badge-release-quality-code-analysis.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.quality.code.analysis)

The plugin helps to analyse code of your project and warns about issues. It makes use of the [detekt plugin](https://github.com/detekt/detekt).

## Features

As this plugin wraps detekt, it offers the same features for project configuration.

## Installation

Add the following to your project root `build.gradle/build.gradle.kts` file:

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
