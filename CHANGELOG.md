# Changelog

All notable changes to this project will be documented in this file.

The format is based on [keep a changelog](http://keepachangelog.com/en/1.0.0/) and using following
types of changes: `Added`, `Changed`, `Deprecated`, `Removed`, `Fixed`, `Security`, `Bumped` and `Migration`.

This project adheres to [semantic versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased](https://github.com/bitfunk/gradle-plugins/releases/latest)

See [changeset](https://github.com/bitfunk/gradle-plugins/compare/v0.1.0...main)

### Bumped

- jacksonToml 2.14.0 -> 2.14.1

## [0.1.0](https://github.com/bitfunk/gradle-plugins/releases/tag/v0.1.0)

Initial release

### Added

- Add initial project
- Add Formatter Plugin - using Spotless and ktlint
- Add GradleWrapper validation GitHub Action
- Add Sonarcloud analysis as part of the pull-request validation to enable coverage reports
- Add CodeAnalysis Plugin - using Detekt
- Add Versioning Plugin - a versioning plugin using git tags
- Add Report Plugin - a quality report plugin using sonarqube
- Add Quality Plugin - a collection of all quality plugins
- Add Code of Conduct
- Add Publish Plugin - a publish plugin for maven dependencies
- Add Composite Delegator Plugin - a plugin that delegates gradle tasks execution to included builds
- Add maven central publishing to Gradle Plugin Convention

### Fixed

- Versioning: cleaned version wrong for feature versions

### Bumped

- Sonarqube Gradle plugin 3.4.0.2513 -> 3.5.0.2730

## Gradle plugin development

### Plugin

- [GradlePluginConvention](plugin-development/gradle-plugin-convention/CHANGELOG.md)
- [VersionCatalogAccessor](./plugin-development/version-catalog-accessor/CHANGELOG.md)

### Test

- [GradleTestUtil](plugin-development/gradle-test-util/CHANGELOG.md)
