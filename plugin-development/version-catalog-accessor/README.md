# VersionCatalogAccessor

[![GradleVersionCatalogAccessor](../../docs/assets/images/badge-release-gradle-version-catalog-accessor.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.development.version.catalog.accessor)

This plugin generates an accessor to expose
the [version catalog (toml)](https://docs.gradle.org/current/userguide/platforms.html) content to
Gradle plugins. This allows plugins to reuse an already available version catalog in it's own
implementation similarly to typesafe accessors.

## Requirements

- Gradle 7.2+
- Can only be applied to Gradle plugins with the java-gradle-plugin available.
- Kotlin DSL
- A version catalog available and enabled (default: `libs.versions.toml`)

## Installation

Add the following to your project module `build.gradle/build.gradle.kts` file:

```kotlin
plugins {
    id("eu.bitfunk.gradle.plugin.development.version.catalog.accessor")
}
```

The plugin works with a default version catalog setup, but could be configured to your needs:

- **_catalogSourceFolder_**: relative path to the version catalog toml files. Default: `gradle/`
- **_catalogNames_**: names of the catalogs for whom to create an accessor.
  Default: `listOf("libs")`
- **_packageName_**: package used for the generated accessor. Default: empty

```kotlin
versionCatalogHelper {
    catalogSourceFolder.set("")
    catalogNames.set(listOf("libs", "deps"))
    packageName.set("com.example")
}
```

## Usage

Generate the version catalog accessor. When your version catalog is named `libs` it will
be `LibsVersionCatalogAccessor`.

```bash
./gradlew generateVersionCatalogAccessor
```

When the accessor is generated, it is added to your project sourceSet and available to be used
within your plugin. Import it and use it to access versions, libraries, bundles and plugins from
your version catalog.

```kotlin
val libs = LibsVersionCatalogAccessor(project)

config {
    libVersion = libs.versions.example.get()
    libVersion = libs.versions.example.getStatic()
}

dependencies {
    implementation(libs.library.get())
}
```

If you're consuming a version like `LibsVersionCatalogAccessor(project).versions.exmaple.get()`, it
will be a dynamic version. This version needs to be defined in any consuming project. Alternatively
you could use `LibsVersionCatalogAccessor(project).versions.exmaple.getStatic()` for a fixed version
bound to your local version catalog.
