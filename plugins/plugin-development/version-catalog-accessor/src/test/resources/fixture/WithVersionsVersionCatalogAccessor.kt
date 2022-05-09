package com.example.catalog.generated

import com.example.catalog.generated.WithVersionsVersionCatalogAccessorContract.Bundles
import com.example.catalog.generated.WithVersionsVersionCatalogAccessorContract.Libraries
import com.example.catalog.generated.WithVersionsVersionCatalogAccessorContract.Plugins
import com.example.catalog.generated.WithVersionsVersionCatalogAccessorContract.Versions
import kotlin.String
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

public interface WithVersionsVersionCatalogAccessorContract {
    public interface Versions {
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

    public interface Libraries

    public interface Bundles

    public interface Plugins
}

public class WithVersionsVersionCatalogAccessor(
    project: Project
) : Libraries {
    private val versionCatalog: VersionCatalog =
            project.extensions.getByType(VersionCatalogsExtension::class.java).named("with-versions")

    public val versions: Versions = object : Versions {
        public override val example: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findVersion("example")
        }

        public override val group: Versions.Group = object : Versions.Group {
            public override val example: Versions.Group.Example = object : Versions.Group.Example {
                public override val one: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String = findVersion("group-example-one")
                }

                public override val two: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String = findVersion("group-example-two")
                }

                public override fun `get`(): String = findVersion("group-example")
            }
        }
    }

    public val bundles: Bundles = object : Bundles {
    }

    public val plugins: Plugins = object : Plugins {
    }

    private fun findVersion(name: String): String {
        try {
            return versionCatalog.findVersion(name).get().requiredVersion
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't findVersion accessor in with-versions.versions.toml: $name"
            )
        }
    }

    private fun findLibrary(name: String): String {
        try {
            return versionCatalog.findLibrary(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't findLibrary accessor in with-versions.versions.toml: $name"
            )
        }
    }

    private fun findBundle(name: String): String {
        try {
            return versionCatalog.findBundle(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't findBundle accessor in with-versions.versions.toml: $name"
            )
        }
    }

    private fun findPlugin(name: String): String {
        try {
            return versionCatalog.findPlugin(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't findPlugin accessor in with-versions.versions.toml: $name"
            )
        }
    }
}
