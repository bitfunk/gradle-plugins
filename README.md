<!--local-files-->

[changelog]: CHANGELOG.md
[code of conduct]: CODE_OF_CONDUCT.md
[contributing]: CONTRIBUTING.md
[license]: LICENSE

<!--readme-start-->
<!--local-links-overwrite-->

[changelog]: docs/src/changelog.md
[code of conduct]: docs/src/develop/codeOfConduct.md
[contributing]: docs/src/develop/contributing.md
[license]: docs/src/license.md

<!--docs-links-->

[plugins]: docs/src/plugins/index.md
[contributing]: docs/src/develop/contributing.md
[releasing]: docs/src/develop/releasing.md

<!--plugin-links-->

[quality all]: docs/src/plugins/quality/index.md
[code analysis]: docs/src/plugins/quality/code-analysis/index.md
[formatter]: docs/src/plugins/quality/formatter/index.md
[report]: docs/src/plugins/quality/report/index.md
[composite delegator]: docs/src/plugins/tool/compositeDelegator/index.md
[publish]: docs/src/plugins/tool/publish/index.md
[versioning]: docs/src/plugins/tool/versioning/index.md

<!--plugin-development-links-->

[versioncatalog accessor]: docs/src/pluginDevelopment/versionCatalogAccessor/index.md
[gradle plugin convention]: docs/src/pluginDevelopment/gradlePluginConvention/index.md

<!--github-links-->

[webpage]: https://bitfunk.github.io/gradle-plugins/
[repository]: https://github.com/bitfunk/gradle-plugins
[issues]: https://github.com/bitfunk/gradle-plugins/issues
[releases]: https://github.com/bitfunk/gradle-plugins/releases

![Logo](docs/src/assets/images/logo.png)

# Bitfunk Gradle Plugins

[![Section quality](docs/src/assets/images/badge-section-quality.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.quality)
[![QualityCodeAnalysis](docs/src/assets/images/badge-release-quality-code-analysis.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.quality.code.analysis)
[![QualityCodeFormatter](docs/src/assets/images/badge-release-quality-code-formatter.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.quality.formatter)
[![QualityReport](docs/src/assets/images/badge-release-quality-report.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.quality.report)

[![Section tool](docs/src/assets/images/badge-section-tool.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.tool)
[![ToolCompositeDelegator](docs/src/assets/images/badge-release-tool-composite-delegator.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.tool.composite.delegator)
[![ToolGitVersion](docs/src/assets/images/badge-release-tool-git-version.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.tool.gitversion)
[![ToolVersioning](docs/src/assets/images/badge-release-tool-versioning.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.tool.versioning)

![Section plugin development](docs/src/assets/images/badge-section-plugin-development.svg)
[![GradlePluginConvention](docs/src/assets/images/badge-release-gradle-plugin-convention.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.development.convention)
[![GradleVersionCatalogAccessor](docs/src/assets/images/badge-release-gradle-version-catalog-accessor.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.development.version.catalog.accessor)
[![GradleTestUtil](docs/src/assets/images/badge-release-gradle-test-util.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.development.test)

[![CI - Build Snapshot Version](https://github.com/bitfunk/gradle-plugins/actions/workflows/ci-build-snapshot-version.yml/badge.svg)](https://github.com/bitfunk/gradle-plugins/actions/workflows/ci-build-snapshot-version.yml)
[![Quality](https://sonarcloud.io/api/project_badges/measure?project=bitfunk_gradle-plugins&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=bitfunk_gradle-plugins)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=bitfunk_gradle-plugins&metric=coverage)](https://sonarcloud.io/summary/new_code?id=bitfunk_gradle-plugins)
[![Tech debt](https://sonarcloud.io/api/project_badges/measure?project=bitfunk_gradle-plugins&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=bitfunk_gradle-plugins)

[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/6013/badge)](https://bestpractices.coreinfrastructure.org/projects/6013)
[![License](docs/src/assets/images/badge-license.svg)](LICENSE)

## About the project

A Collection of Gradle plugins to simplify and unify project development used across multiple bitfunk projects. They add opinionated configuration for other plugins or new functionality.

### Plugins

#### Quality

- [Quality All] - a collection of all quality plugins
- [Code Analysis] - a source code analysis plugin
- [Formatter] - a source code formatter plugin applying bitfunk code style
- [Report] - a quality report plugin using sonarqube

#### Tool

- [Composite Delegator] - a plugin that delegates gradle tasks execution to included builds
- [Publish] - a publish plugin for maven publications
- [Versioning] - a versioning plugin using git tags

### Plugin development

- [Gradle Plugin Convention] - a convention plugin for Gradle plugin development to ease configuration
- [VersionCatalog Accessor] - a version catalog accessor for Gradle plugin development

## Getting started

You could use individual plugins or the all-plugins collection.

Check the usage of each Gradle plugin by reading the [plugin documentation][plugins].

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

All notable changes to this project will be documented in the [changelog].

## Versioning

We use [Semantic Versioning](http://semver.org/) as a guideline for our versioning.

## Contributing

You want to help or share a proposal? You have a specific problem? [Report a bug][issues] or [request a feature][issues].

You want to fix or change code? Read the [Code of Conduct] and [contributing guide][contributing].

## Releasing

See [releasing].

## Copyright and license

Copyright (c) 2022 Wolf-Martell Montwé.

Please refer to the [ISC License][license] for more information.

<!--readme-end-->
