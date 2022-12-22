# Changelog

All notable changes to this project will be documented in this file.

The format is based on [keep a changelog](http://keepachangelog.com/en/1.0.0/) and using following
types of changes: `Added`, `Changed`, `Deprecated`, `Removed`, `Fixed`, `Security`, `Bumped` and `Migration`.

This project adheres to [semantic versioning](http://semver.org/spec/v2.0.0.html).

Changelogs for every plugin are listed below and for this project could be found in the [project](#project) section at the end.

## Plugin

### Quality

- [QualityCollection](plugins/quality/CHANGELOG.md)
- [CodeAnalysis](plugins/quality/code-analysis/CHANGELOG.md)
- [Report](plugins/quality/report/CHANGELOG.md)
- [Formatter](plugins/quality/formatter/CHANGELOG.md)

### Tool

- [CompositeDelegator](plugins/tool/composite-delegator/CHANGELOG.md)
- [Publish](plugins/tool/publish/CHANGELOG.md)
- [GitVersion](plugins/tool/git-version/CHANGELOG.md)
- [Versioning](plugins/tool/versioning/CHANGELOG.md)

## Gradle plugin development

### Plugin

- [GradlePluginConvention](plugin-development/gradle-plugin-convention/CHANGELOG.md)
- [VersionCatalogAccessor](./plugin-development/version-catalog-accessor/CHANGELOG.md)

### Test

- [GradleTestUtil](plugin-development/gradle-test-util/CHANGELOG.md)

## Project

All project changes below.

## [Unreleased](https://github.com/bitfunk/gradle-plugins/releases/latest)

See [changeset](https://github.com/bitfunk/gradle-plugins/compare/v0.1.0...main)

### Changed

- Change documentation from multi version to single version

### Bumped

- Bitfunk Quality 0.1.0 -> 0.1.2
- Bitfunk Versioning 0.1.1 -> 0.1.2

## [0.1.0](https://github.com/bitfunk/gradle-plugins/releases/tag/v0.1.0)

### Added

- Add initial project
- Add GradleWrapper validation GitHub Action
- Add Sonarcloud analysis as part of the pull-request validation to enable coverage reports
- Add Code of Conduct
- Add test report uploader
