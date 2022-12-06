# Gradle Plugin Convention

[![GradlePluginConvention](../../docs/assets/images/badge-release-gradle-plugin-convention.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.development.convention)

The plugin applies configuration for Gradle plugin development to ease configuration effort. It sets Java 11 support, default dependencies, tests and coverage settings. It also enables [explicit API mode for Kotlin](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors).

## Requirements

- Gradle 7.2+

## Installation

Add the following to your project module `build.gradle/build.gradle.kts` file:

```kotlin
plugins {
    id("eu.bitfunk.gradle.plugin.development.convention")
}
```

The plugin configures publishing and needs to be setup:

- **_publishName_**: Name of the plugin
- **_publishDescription_**: Description of the plugin
- **_publishGitHubOrganization_**: GitHub organisation/account name
- **_publishGitHubRepositoryName_**: GitHub repository name

```kotlin
projectConfig {
    publishName.set("PLUGIN_NAME")
    publishDescription.set("PLUGIN_DESCRIPTION")
    publishGitHubOrganization.set("PLUGIN_GITHUB_ORG")
    publishGitHubRepositoryName.set("PLUGIN_GITHUB_REPOSITORY")
}
```

## Usage

When the plugin is applied, the project is configured with JUnit5 for testing and Kotlin as development language.

Just start to develop and test your GradlePlugin with minimal configuration effort.
