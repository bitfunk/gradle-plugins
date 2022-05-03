<!--local-files-->
[changelog]: CHANGELOG.md
[license]: LICENSE
<!--readme-start-->
<!--local-links-overwrite-->
[changelog]: docs/src/changelog.md
[license]: docs/src/license.md

<!--docs-links-->
[plugins]: docs/src/plugins/index.md
[contributing]: docs/src/develop/contributing.md
[releasing]: docs/src/develop/releasing.md

<!--plugin-links-->
[Gradle Plugin Convention]: docs/src/plugins/pluginDevelopment/gradlePluginConvention/index.md
[VersionCatalogAccessor]: docs/src/plugins/pluginDevelopment/versionCatalogAccessor/index.md

<!--github-links-->
[webpage]: https://bitfunk.github.io/gradle-plugins/
[repository]: https://github.com/bitfunk/gradle-plugins
[issues]: https://github.com/bitfunk/gradle-plugins/issues
[releases]: https://github.com/bitfunk/gradle-plugins/releases

![Logo](docs/src/assets/images/logo.png)

# Bitfunk Gradle Plugins

A Collection of Gradle plugins to simplify and unify project development.

[![Latest release](docs/src/assets/images/badge-release-latest.svg)][releases]
[![License](docs/src/assets/images/badge-license.svg)](LICENSE)
[![CI - Build Latest Version](https://github.com/bitfunk/gradle-plugins/actions/workflows/ci-build-latest-version.yml/badge.svg)](https://github.com/bitfunk/gradle-plugins/actions/workflows/ci-build-latest-version.yml)

[![Quality](https://sonarcloud.io/api/project_badges/measure?project=bitfunk_gradle-plugins&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=bitfunk_gradle-plugins)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=bitfunk_gradle-plugins&metric=coverage)](https://sonarcloud.io/summary/new_code?id=bitfunk_gradle-plugins)
[![Tech debt](https://sonarcloud.io/api/project_badges/measure?project=bitfunk_gradle-plugins&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=bitfunk_gradle-plugins)
[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/6013/badge)](https://bestpractices.coreinfrastructure.org/projects/6013)

## About the project

Collection of Gradle plugins used across multiple projects. They add opinionated configuration for other plugins or new functionality.

### Plugins

#### Plugin development:

* [Gradle Plugin Convention] - a convention plugin for Gradle plugin development to ease configuration
* [VersionCatalogAccessor] - a version catalog accessor for Gradle plugin development

## Getting started

You could use individual plugins or the all-plugins collection.

Check the usage of each Gradle plugin by reading the [plugin documentation][plugins].

### Requirements

* Gradle 7.2+
* Java 11

## Installation

Add to your project root `build.gradle/build.gradle.kts` file to download from [GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages#authenticating-to-github-packages=):

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
    id("eu.bitfunk.gradle.plugins")
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

You want to help or share a proposal? You have a specific problem? [Report a bug][issues] or [request a feature][issues].

You want to fix or change code? Read the [contributing guide][contributing].

## Releasing

See [releasing].

## Copyright and license

Copyright (c) 2022 Wolf-Martell Montwé.

Please refer to the [ISC License][license] for more information.

<!--readme-end-->
