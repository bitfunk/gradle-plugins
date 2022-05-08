package com.example.catalog.generated

import com.example.catalog.generated.WithBundlesVersionCatalogAccessorContract.Bundles
import com.example.catalog.generated.WithBundlesVersionCatalogAccessorContract.Libraries
import com.example.catalog.generated.WithBundlesVersionCatalogAccessorContract.Plugins
import com.example.catalog.generated.WithBundlesVersionCatalogAccessorContract.Versions
import kotlin.String
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

public interface WithBundlesVersionCatalogAccessorContract {
    public interface Versions

    public interface Libraries

    public interface Bundles {
        public val example: VersionCatalogDependency.Leaf

        public val group: Group

        public interface Group : VersionCatalogDependency.Group {
            public val example: Example

            public interface Example : VersionCatalogDependency.GroupLeaf {
                public val one: VersionCatalogDependency.Leaf

                public val two: VersionCatalogDependency.Leaf
            }
        }
    }

    public interface Plugins
}

public class WithBundlesVersionCatalogAccessor(
    project: Project
) : Libraries {
    private val versionCatalog: VersionCatalog =
            project.extensions.getByType(VersionCatalogsExtension::class.java).named("with-bundles")

    public val versions: Versions = object : Versions {
    }

    public val bundles: Bundles = object : Bundles {
        public override val example: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findBundle("example")
        }

        public override val group: Bundles.Group = object : Bundles.Group {
            public override val example: Bundles.Group.Example = object : Bundles.Group.Example {
                public override val one: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String = findBundle("group-example-one")
                }

                public override val two: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String = findBundle("group-example-two")
                }

                public override fun `get`(): String = findBundle("group-example")
            }
        }
    }

    public val plugins: Plugins = object : Plugins {
    }

    private fun findVersion(name: String): String {
        try {
            return versionCatalog.findVersion(name).get().requiredVersion
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't find accessor in with-bundles.versions.toml: $name"
            )
        }
    }

    private fun findLibrary(name: String): String {
        try {
            return versionCatalog.findLibrary(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't find accessor in with-bundles.versions.toml: $name"
            )
        }
    }

    private fun findBundle(name: String): String {
        try {
            return versionCatalog.findBundle(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't find accessor in with-bundles.versions.toml: $name"
            )
        }
    }

    private fun findPlugin(name: String): String {
        try {
            return versionCatalog.findPlugin(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't find accessor in with-bundles.versions.toml: $name"
            )
        }
    }
}
