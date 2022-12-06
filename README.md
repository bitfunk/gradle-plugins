[quality all]: docs/src/plugins/quality/index.md
[code analysis]: docs/src/plugins/quality/code-analysis/index.md
[formatter]: docs/src/plugins/quality/formatter/index.md
[report]: docs/src/plugins/quality/report/index.md
[composite delegator]: docs/src/plugins/tool/compositeDelegator/index.md

<!--github-links-->

[webpage]: https://bitfunk.github.io/gradle-plugins/
[repository]: https://github.com/bitfunk/gradle-plugins
[issues]: https://github.com/bitfunk/gradle-plugins/issues
[releases]: https://github.com/bitfunk/gradle-plugins/releases

![Logo](docs/assets/images/logo.png)

# Bitfunk Gradle Plugins

[![Section quality](docs/assets/images/badge-section-quality.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.quality)
[![QualityCodeAnalysis](docs/assets/images/badge-release-quality-code-analysis.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.quality.code.analysis)
[![QualityCodeFormatter](docs/assets/images/badge-release-quality-code-formatter.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.quality.formatter)
[![QualityReport](docs/assets/images/badge-release-quality-report.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.quality.report)

[![Section tool](docs/assets/images/badge-section-tool.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.tool)
[![ToolCompositeDelegator](docs/assets/images/badge-release-tool-composite-delegator.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.tool.composite.delegator)
[![ToolPublish](docs/assets/images/badge-release-tool-publish.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.tool.publish)
[![ToolGitVersion](docs/assets/images/badge-release-tool-git-version.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.tool.gitversion)
[![ToolVersioning](docs/assets/images/badge-release-tool-versioning.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.tool.versioning)

![Section plugin development](docs/assets/images/badge-section-plugin-development.svg)
[![GradlePluginConvention](docs/assets/images/badge-release-gradle-plugin-convention.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.development.convention)
[![GradleVersionCatalogAccessor](docs/assets/images/badge-release-gradle-version-catalog-accessor.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.development.version.catalog.accessor)
[![GradleTestUtil](docs/assets/images/badge-release-gradle-test-util.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.development.test)

[![CI - Build Snapshot Version](https://github.com/bitfunk/gradle-plugins/actions/workflows/ci-build-snapshot-version.yml/badge.svg)](https://github.com/bitfunk/gradle-plugins/actions/workflows/ci-build-snapshot-version.yml)
[![Quality](https://sonarcloud.io/api/project_badges/measure?project=bitfunk_gradle-plugins&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=bitfunk_gradle-plugins)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=bitfunk_gradle-plugins&metric=coverage)](https://sonarcloud.io/summary/new_code?id=bitfunk_gradle-plugins)
[![Tech debt](https://sonarcloud.io/api/project_badges/measure?project=bitfunk_gradle-plugins&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=bitfunk_gradle-plugins)

[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/6013/badge)](https://bestpractices.coreinfrastructure.org/projects/6013)
[![License](docs/assets/images/badge-license.svg)](LICENSE.md)

## About the project

A Collection of Gradle plugins to simplify and unify project development used across multiple bitfunk projects. They add opinionated configuration for other plugins or new functionality.

### Plugins

Plugins and tools that support developing software and add functionality to Gradle.

#### Quality

- [Quality All] - a collection of all quality plugins
- [Code Analysis] - a source code analysis plugin
- [Formatter] - a source code formatter plugin applying bitfunk code style
- [Report] - a quality report plugin using sonarqube

#### Tool

- [Composite Delegator] - a plugin that delegates gradle tasks execution to included builds
- [Publish](plugins/tool/publish/README.md) - a publish plugin for maven publications
- [Versioning](plugins/tool/versioning/README.md) - a versioning plugin using git tags

### Plugin development

Plugins and tools that support developing Gradle plugins.

#### Plugin

- [Gradle Plugin Convention](plugin-development/gradle-plugin-convention/README.md) - a convention plugin for Gradle plugin development to ease configuration
- [VersionCatalogAccessor](plugin-development/version-catalog-accessor/README.md) - a version catalog accessor for Gradle plugin development

#### Test

- [GradleTestUtil](plugin-development/gradle-test-util/README.md) - Test util to ease Gradle plugin testing

## Getting started

You could use individual plugins or the all-plugins collection.

Check the usage of each Gradle plugin by reading the [plugin documentation](plugins/README.md).

### Requirements

- Gradle 7.2+
- Java 11

## Installation

The dependencies are distributed through [Maven Central](https://central.sonatype.dev/) In case you want to use a SNAPSHOT version, add the following to your project root `build.gradle/build.gradle.kts` file:

```kotlin
buildscript {
    repositories {
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}
```

## Usage

Apply the plugins of your choice to your project. For details refer to the [plugins overview][plugins].

## Roadmap

This project is work in progress. We are working on adding more functionality, guidelines, documentation and other improvements.

See the open [issues] for a list of proposed improvements and known issues.

## Changelog

All notable changes to this project will be documented in the [changelog](CHANGELOG.md).

## Versioning

We use [Semantic Versioning](http://semver.org/) as a guideline for our versioning.

## Contributing

You want to help or share a proposal? You have a specific problem? [Report a bug][issues] or [request a feature][issues].

You want to fix or change code? Read the [Code of Conduct](CODE_OF_CONDUCT.md) and [contributing guide](CONTRIBUTING.md).

## Releasing

See [releasing](docs/develop/RELEASING.md).

## Copyright and license

Copyright (c) 2022 Wolf-Martell Montwé.

Please refer to the [ISC License][license] for more information.

<!--readme-end-->
