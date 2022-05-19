# Composite Delegator Plugin

A plugin that delegates gradle task execution to included builds.

## Features

* standard tasks are already added: `assemble`, `build`, `check`, `test`
* additional task could be configured

## Installation

Add to your project root `build.gradle/build.gradle.kts` file to download from GitHub packages:

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
    id("eu.bitfunk.gradle.plugin.tool.composite.delegator")
}
```

The plugin could delegate additional tasks:

- **_additionalTasks_**: List of task names that additionally should be delegated to included builds

```kotlin
compositeDelegator {
    additionalTasks.set(listOf("task1", "task2"))
}
```

## Usage

Ensure that your desired task is registered and call:

```bash
./gradlew exampleTask
```
