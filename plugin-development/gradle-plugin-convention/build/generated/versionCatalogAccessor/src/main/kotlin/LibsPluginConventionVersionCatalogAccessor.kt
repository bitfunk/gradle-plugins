package eu.bitfunk.gradle.plugin.development.convention.libs.generated

import eu.bitfunk.gradle.plugin.development.convention.libs.generated.LibsPluginConventionVersionCatalogAccessorContract.Bundles
import eu.bitfunk.gradle.plugin.development.convention.libs.generated.LibsPluginConventionVersionCatalogAccessorContract.Libraries
import eu.bitfunk.gradle.plugin.development.convention.libs.generated.LibsPluginConventionVersionCatalogAccessorContract.Plugins
import eu.bitfunk.gradle.plugin.development.convention.libs.generated.LibsPluginConventionVersionCatalogAccessorContract.Versions
import kotlin.String
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

public interface LibsPluginConventionVersionCatalogAccessorContract {
    public interface Versions {
        public val gradle: VersionCatalogDependency.Leaf

        public val kotlin: VersionCatalogDependency.Leaf

        public val test: Test

        public val plugin: Plugin

        public interface Test : VersionCatalogDependency.Group {
            public val jUnit5: VersionCatalogDependency.Leaf

            public val mockk: VersionCatalogDependency.Leaf

            public val jacoco: VersionCatalogDependency.Leaf

            public val gradleTestUtil: VersionCatalogDependency.Leaf
        }

        public interface Plugin : VersionCatalogDependency.Group {
            public val kotlin: Kotlin

            public val mavenPublish: VersionCatalogDependency.Leaf

            public val versionCatalogAccessor: VersionCatalogDependency.Leaf

            public interface Kotlin : VersionCatalogDependency.Group {
                public val gradleDsl: VersionCatalogDependency.Leaf

                public val binaryCompatibility: VersionCatalogDependency.Leaf
            }
        }
    }

    public interface Libraries {
        public val plugin: Plugin

        public val test: Test

        public interface Plugin : VersionCatalogDependency.Group {
            public val kotlin: VersionCatalogDependency.Leaf

            public val kotlinBinaryCompatibility: VersionCatalogDependency.Leaf

            public val kotlinDsl: VersionCatalogDependency.Leaf

            public val mavenPublish: VersionCatalogDependency.Leaf
        }

        public interface Test : VersionCatalogDependency.Group {
            public val jUnit5: JUnit5

            public val kotlin: VersionCatalogDependency.Leaf

            public val mockk: VersionCatalogDependency.Leaf

            public val gradleTestUtil: VersionCatalogDependency.Leaf

            public interface JUnit5 : VersionCatalogDependency.GroupLeaf {
                public val engine: VersionCatalogDependency.Leaf
            }
        }
    }

    public interface Bundles

    public interface Plugins {
        public val kotlin: Kotlin

        public val mavenPublishPlugin: VersionCatalogDependency.Leaf

        public val versionCatalogAccessor: VersionCatalogDependency.Leaf

        public interface Kotlin : VersionCatalogDependency.Group {
            public val binaryCompatibilityValidator: VersionCatalogDependency.Leaf
        }
    }
}

public class LibsPluginConventionVersionCatalogAccessor(
    private val project: Project,
) : Libraries {
    private val versionCatalog: VersionCatalog
        get() = project.extensions.getByType(VersionCatalogsExtension::class.java)
            .named("libs-plugin-convention")

    public val versions: Versions = object : Versions {
        public override val gradle: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findVersion("gradle")

            public override fun getStatic(): String = "8.0"
        }

        public override val kotlin: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findVersion("kotlin")

            public override fun getStatic(): String = "1.8.10"
        }

        public override val test: Versions.Test = object : Versions.Test {
            public override val jUnit5: VersionCatalogDependency.Leaf = object :
                    VersionCatalogDependency.Leaf {
                public override fun `get`(): String = findVersion("test-jUnit5")

                public override fun getStatic(): String = "5.9.2"
            }

            public override val mockk: VersionCatalogDependency.Leaf = object :
                    VersionCatalogDependency.Leaf {
                public override fun `get`(): String = findVersion("test-mockk")

                public override fun getStatic(): String = "1.13.4"
            }

            public override val jacoco: VersionCatalogDependency.Leaf = object :
                    VersionCatalogDependency.Leaf {
                public override fun `get`(): String = findVersion("test-jacoco")

                public override fun getStatic(): String = "0.8.8"
            }

            public override val gradleTestUtil: VersionCatalogDependency.Leaf = object :
                    VersionCatalogDependency.Leaf {
                public override fun `get`(): String = findVersion("test-gradleTestUtil")

                public override fun getStatic(): String = "0.2.0"
            }
        }

        public override val plugin: Versions.Plugin = object : Versions.Plugin {
            public override val kotlin: Versions.Plugin.Kotlin = object : Versions.Plugin.Kotlin {
                public override val gradleDsl: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String = findVersion("plugin-kotlin-gradleDsl")

                    public override fun getStatic(): String = "4.0.6"
                }

                public override val binaryCompatibility: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String =
                            findVersion("plugin-kotlin-binaryCompatibility")

                    public override fun getStatic(): String = "0.13.0"
                }
            }

            public override val mavenPublish: VersionCatalogDependency.Leaf = object :
                    VersionCatalogDependency.Leaf {
                public override fun `get`(): String = findVersion("plugin-mavenPublish")

                public override fun getStatic(): String = "0.24.0"
            }

            public override val versionCatalogAccessor: VersionCatalogDependency.Leaf = object :
                    VersionCatalogDependency.Leaf {
                public override fun `get`(): String = findVersion("plugin-versionCatalogAccessor")

                public override fun getStatic(): String = "0.2.0"
            }
        }
    }

    public val bundles: Bundles = object : Bundles {
    }

    public val plugins: Plugins = object : Plugins {
        public override val kotlin: Plugins.Kotlin = object : Plugins.Kotlin {
            public override val binaryCompatibilityValidator: VersionCatalogDependency.Leaf = object
                    : VersionCatalogDependency.Leaf {
                public override fun `get`(): String =
                        findPlugin("kotlin-binaryCompatibilityValidator")

                public override fun getStatic(): String = throw UnsupportedOperationException(
                    "not yet implemented"
                )
            }
        }

        public override val mavenPublishPlugin: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findPlugin("mavenPublishPlugin")

            public override fun getStatic(): String = throw UnsupportedOperationException(
                "not yet implemented"
            )
        }

        public override val versionCatalogAccessor: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findPlugin("versionCatalogAccessor")

            public override fun getStatic(): String = throw UnsupportedOperationException(
                "not yet implemented"
            )
        }
    }

    public override val plugin: Libraries.Plugin = object : Libraries.Plugin {
        public override val kotlin: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findLibrary("plugin-kotlin")

            public override fun getStatic(): String = throw UnsupportedOperationException(
                "not yet implemented"
            )
        }

        public override val kotlinBinaryCompatibility: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findLibrary("plugin-kotlinBinaryCompatibility")

            public override fun getStatic(): String = throw UnsupportedOperationException(
                "not yet implemented"
            )
        }

        public override val kotlinDsl: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findLibrary("plugin-kotlinDsl")

            public override fun getStatic(): String = throw UnsupportedOperationException(
                "not yet implemented"
            )
        }

        public override val mavenPublish: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findLibrary("plugin-mavenPublish")

            public override fun getStatic(): String = throw UnsupportedOperationException(
                "not yet implemented"
            )
        }
    }

    public override val test: Libraries.Test = object : Libraries.Test {
        public override val jUnit5: Libraries.Test.JUnit5 = object : Libraries.Test.JUnit5 {
            public override val engine: VersionCatalogDependency.Leaf = object :
                    VersionCatalogDependency.Leaf {
                public override fun `get`(): String = findLibrary("test-jUnit5-engine")

                public override fun getStatic(): String = throw UnsupportedOperationException(
                    "not yet implemented"
                )
            }

            public override fun `get`(): String = findLibrary("test-jUnit5")

            public override fun getStatic(): String = throw UnsupportedOperationException(
                "not yet implemented"
            )
        }

        public override val kotlin: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findLibrary("test-kotlin")

            public override fun getStatic(): String = throw UnsupportedOperationException(
                "not yet implemented"
            )
        }

        public override val mockk: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findLibrary("test-mockk")

            public override fun getStatic(): String = throw UnsupportedOperationException(
                "not yet implemented"
            )
        }

        public override val gradleTestUtil: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findLibrary("test-gradleTestUtil")

            public override fun getStatic(): String = throw UnsupportedOperationException(
                "not yet implemented"
            )
        }
    }

    private fun findVersion(name: String): String {
        try {
            return versionCatalog.findVersion(name).get().requiredVersion
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't findVersion accessor in Libs-plugin-convention.versions.toml: $name"
            )
        }
    }

    private fun findLibrary(name: String): String {
        try {
            return versionCatalog.findLibrary(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't findLibrary accessor in Libs-plugin-convention.versions.toml: $name"
            )
        }
    }

    private fun findBundle(name: String): String {
        try {
            return versionCatalog.findBundle(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't findBundle accessor in Libs-plugin-convention.versions.toml: $name"
            )
        }
    }

    private fun findPlugin(name: String): String {
        try {
            return versionCatalog.findPlugin(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't findPlugin accessor in Libs-plugin-convention.versions.toml: $name"
            )
        }
    }
}
