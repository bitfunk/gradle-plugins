# GradleTestUtil

This collection of utils simplifies testing of Gradle plugins.

## Installation

The dependency is distributed through [Maven Central](https://central.sonatype.dev/) In case you want to use a SNAPSHOT version, add the following to your project module `build.gradle/build.gradle.kts` file:

```kotlin
dependency {
    testImplementation("eu.bitfunk.gradle.plugin.development.test:gradle-test-util:LATEST_VERSION")
}
```

## Usage


### Stub Gradle action

For easier mocking of Gradle actions with [MockK](https://mockk.io/) there are two util functions provided: `stubGradleAction` and `stubGradleActionWithReturn`.

In case the Gradle DSL you're able to provide a mocked version that will be invoked to test it's usage.

```kotlin
stubGradleAction(publishingExtension) {
    project.extensions.configure(PublishingExtension::class.java, it)
}
```

and in case the action needs to return a value

```kotlin
stubGradleActionWithReturn(jacocoReport, returnedObject) {
    taskContainer.named("jacocoTestReport", JacocoReport::class.java, it)
}
```
