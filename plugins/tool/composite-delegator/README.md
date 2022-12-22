# Composite Delegator Plugin

[![ToolCompositeDelegator](../../../docs/assets/images/badge-release-tool-composite-delegator.svg)](https://central.sonatype.dev/namespace/eu.bitfunk.gradle.plugin.tool.composite.delegator)

A plugin that delegates gradle task execution to included builds.

## Features

- standard tasks are already added: `assemble`, `build`, `check` and `test`
- additional task could be configured

## Installation

Add the following to your project module `build.gradle/build.gradle.kts` file:

```kotlin
plugins {
    id("eu.bitfunk.gradle.plugin.tool.composite.delegator")
}
```

The plugin could delegate additional tasks to included builds:

- **_additionalTasks_**: List of task names that additionally should be delegated to included builds

```kotlin
compositeDelegator {
    additionalTasks.set(listOf("exampleTask", "otherExampleTask"))
}
```

## Usage

Ensure that your desired task is registered and call:

```bash
./gradlew exampleTask
```
