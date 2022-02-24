# Releasing

## Release preparation

Create a release branch of from `main` branch with following pattern:

* `release/{major}.{minor}/prepare-{major}.{minor}.{patch}`

Add following changes:

* Update CHANGELOG.md by creating a new Unreleased section and change current unreleased to release version
* Update the latest release badge [HowTo](../assets/images/badges.md)

## Release

Releases are automatically created with GitHub Actions when a tag in the form of `v{major}.{minor}.{patch}` is added.
