# Releasing

## Release preparation

1. Create a release branch of from `main` branch with this pattern:

* `release/{major}.{minor}/prepare-{major}.{minor}.{patch}`

2. Update CHANGELOG.md by creating a new Unreleased section and change current unreleased to release version
3. Update the latest release [badge](badges.md)

## Release

Releases are automatically created from tags with GitHub Actions.

They need to be in the form of `v{major}.{minor}.{patch}`.
