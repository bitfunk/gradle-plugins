[webpage]: https://bitfunk.github.io/gradle-plugins/
[repository]: https://github.com/bitfunk/gradle-plugins
[issues]: https://github.com/bitfunk/gradle-plugins/issues
[releases]: https://github.com/bitfunk/gradle-plugins/releases

[plugins]: docs/src/plugins/index.md
[changelog]: CHANGELOG.md
[contributing]: docs/src/develop/contributing.md
[releasing]: docs/src/develop/releasing.md
[license]: LICENSE

[VersionCatalogAccessor]: docs/src/plugins/plugin/versionCatalogAccessor/index.md

![Logo](docs/src/assets/images/logo.png)

# Bitfunk Gradle Plugins

A Collection of Gradle plugins to simplify and unify project development.

[Explore the docs »][webpage]

[Report Bug][issues] | [Request Feature][issues]

[![Latest release](docs/src/assets/images/badge-release-latest.svg)][releases]
[![License](docs/src/assets/images/badge-license.svg)](LICENSE)

## About the project

Collection of Gradle plugins used across multiple projects. They add opinionated configuration for other plugins or add new functionality.

### Plugins

* **Plugin development**:

* [VersionCatalogAccessor] - a version catalog accessor for Gradle plugin development

## Getting started

You could use individual plugins or the all-plugins collection.

Check the usage of each Gradle plugin by reading the [plugin documentation][plugins].

### Requirements

* Gradle 7.2+
* Java 11

## Installation

Add to your project root `build.gradle/build.gradle.kts` file to download from [GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages#authenticating-to-github-packages=):

```
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
    id("eu.bitfunk.gradle.all-plugins")
}
```

## Usage

Apply the plugins of your choice to your project. For details refer to the [plugins overview][plugins].

## Roadmap

This project is work in progress. We are working on adding more functionality, guidelines,
documentation and other improvements.

See the open [issues] for a list of proposed improvements and known issues.

## Changelog

All notable changes to this project will be documented in the [changelog].

## Versioning

We use [Semantic Versioning](http://semver.org/) as a guideline for our versioning.

## Contributing

You want to help or share a proposal? You have a specific problem? You want to fix or change code? 

Read the [contributing guide][contributing].

## Releasing

See [releasing].

## Copyright and license

Copyright (c) 2022 Wolf-Martell Montwé.

Please refer to the [ISC License][license] for more information.
