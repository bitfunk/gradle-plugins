# Git Version Plugin

[![ToolGitVersion](../../../docs/assets/images/badge-release-tool-git-version.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.tool.gitversion)

A plugin to generate semantic version information from git tags and make them available for the project this plugin is applied to.

## Features

This plugin mimics `git describe --always` and `git describe --always --match VERSION_PREFIX` to derive a version string. In case the repository state is dirty, a `.dirty` marker is appended to the version string.

It adds:

- gitVersion - version code derived from a git tag
- gitVersionInfo - prints all information about the current version

## Installation

Add the following to your project module `build.gradle/build.gradle.kts` file:

```kotlin
plugins {
    id("eu.bitfunk.gradle.plugin.tool.gitversion")
}
```

The plugin works without configuration, assuming there is no prefix used for version tags. In case you want to use a prefixed version tag, cou could configure it to your needs:

- **_prefix_**: desired version tag prefix, that must comply with this regex `[/@]?([A-Za-z]+[/@-])+`
  Default: empty

```kotlin
gitVersionConfig {
    prefix.set("my-prefix@")
}
```

## Usage

To print current version information:

```bash
./gradlew printGitVersion
./gradlew printGitVersionInfo
```

To access the git version information, use this project extensions:

- `gitVersion()` - git version as String
- `gitVersionInfo()` - git version information as String

## Acknowledgements

This plugin started as a Kotlin rewrite of [gradle-git-version](https://github.com/palantir/gradle-git-version) - a Gradle plugin that uses `git describe` to produce a version string.

During development it turned into its own plugin with adjusted functionality. If you need your version be based on `git describe --tags --always --first-parent` with `first-parent` behavior. Please use the [gradle-git-version](https://github.com/palantir/gradle-git-version) plugin instead.
