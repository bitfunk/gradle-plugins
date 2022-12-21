# Badges

## How To Create Badges

We use [Shields.io](https://shields.io/) to generate Badges.
Please use the [badge links section](#_badge_links) to document and update currently used badges.
Open the links and download the svg files and place them in the projects `assets/images` folder or use `curl` instead.

## Download Badges

Simply use `curl` to download badges by providing the `url` and `filename`, that starts with `badge`.

```bash
curl "link" -s -o badge-filename
```

## Badge Links

### Releases:

#### Quality

```bash
curl "https://img.shields.io/badge/Quality-→-orange.svg?style=flat" -s -o ../assets/images/badge-section-quality.svg
```

- Quality collection
  ```bash
  curl "https://img.shields.io/badge/QualityCollection-v0.1.0-orange.svg?style=flat" -s -o ../assets/images/badge-release-quality-collection.svg
  ```
- Code analysis
  ```bash
  curl "https://img.shields.io/badge/CodeAnalysis-v0.1.0-orange.svg?style=flat" -s -o ../assets/images/badge-release-quality-code-analysis.svg
  ```
- Code Formatter
  ```bash
  curl "https://img.shields.io/badge/CodeFormatter-v0.1.0-orange.svg?style=flat" -s -o ../assets/images/badge-release-quality-code-formatter.svg
  ```
- Report
  ```bash
  curl "https://img.shields.io/badge/Report-v0.1.0-orange.svg?style=flat" -s -o ../assets/images/badge-release-quality-report.svg
  ```

#### Tool

```bash
curl "https://img.shields.io/badge/Tool-→-orange.svg?style=flat" -s -o ../assets/images/badge-section-tool.svg
```

- Composite Delegator
  ```bash
  curl "https://img.shields.io/badge/CompositeDelegator-v0.1.0-orange.svg?style=flat" -s -o ../assets/images/badge-release-tool-composite-delegator.svg
  ```
- Publish
  ```bash
  curl "https://img.shields.io/badge/Publish-TODO-orange.svg?style=flat" -s -o ../assets/images/badge-release-tool-publish.svg
  ```
- Git Version
  ```bash
  curl "https://img.shields.io/badge/GitVersion-v0.1.1-orange.svg?style=flat" -s -o ../assets/images/badge-release-tool-git-version.svg
  ```
- Versioning
  ```bash
  curl "https://img.shields.io/badge/Versioning-v0.1.1-orange.svg?style=flat" -s -o ../assets/images/badge-release-tool-versioning.svg
  ```

#### Plugin-development

```bash
curl "https://img.shields.io/badge/Plugin_Development-→-orange.svg?style=flat" -s -o ../assets/images/badge-section-plugin-development.svg
```

- Gradle plugin convention
  ```bash
  curl "https://img.shields.io/badge/PluginConvention-v0.1.0-orange.svg?style=flat" -s -o ../assets/images/badge-release-gradle-plugin-convention.svg
  ```
- Gradle test util
  ```bash
  curl "https://img.shields.io/badge/TestUtil-v0.1.1-orange.svg?style=flat" -s -o ../assets/images/badge-release-gradle-test-util.svg
  ```
- Gradle Version catalog accessor
  ```bash
  curl "https://img.shields.io/badge/VersionCatalogAccessor-v0.1.1-orange.svg?style=flat" -s -o ../assets/images/badge-release-gradle-version-catalog-accessor.svg
  ```

### Other

- License:
  ```bash
  curl "https://img.shields.io/badge/License-ISC-lightgrey.svg?style=flat" -s -o badge-license.svg
  ```

## License

Shields is licensed under _Creative Commons Zero v1.0 Universal_ (as of 2022-02-23)
