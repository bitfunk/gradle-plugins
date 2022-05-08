package com.example.catalog.generated

import com.example.catalog.generated.WithLibrariesVersionCatalogAccessorContract.Bundles
import com.example.catalog.generated.WithLibrariesVersionCatalogAccessorContract.Libraries
import com.example.catalog.generated.WithLibrariesVersionCatalogAccessorContract.Plugins
import com.example.catalog.generated.WithLibrariesVersionCatalogAccessorContract.Versions
import kotlin.String
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

public interface WithLibrariesVersionCatalogAccessorContract {
    public interface Versions

    public interface Libraries {
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

    public interface Bundles

    public interface Plugins
}

public class WithLibrariesVersionCatalogAccessor(
    project: Project
) : Libraries {
    private val versionCatalog: VersionCatalog =
            project.extensions.getByType(VersionCatalogsExtension::class.java).named("with-libraries")

    public val versions: Versions = object : Versions {
    }

    public val bundles: Bundles = object : Bundles {
    }

    public val plugins: Plugins = object : Plugins {
    }

    public override val example: VersionCatalogDependency.Leaf = object :
            VersionCatalogDependency.Leaf {
        public override fun `get`(): String = findLibrary("example")
    }

    public override val group: Libraries.Group = object : Libraries.Group {
        public override val example: Libraries.Group.Example = object : Libraries.Group.Example {
            public override val one: VersionCatalogDependency.Leaf = object :
                    VersionCatalogDependency.Leaf {
                public override fun `get`(): String = findLibrary("group-example-one")
            }

            public override val two: VersionCatalogDependency.Leaf = object :
                    VersionCatalogDependency.Leaf {
                public override fun `get`(): String = findLibrary("group-example-two")
            }

            public override fun `get`(): String = findLibrary("group-example")
        }
    }

    private fun findVersion(name: String): String {
        try {
            return versionCatalog.findVersion(name).get().requiredVersion
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't find accessor in with-libraries.versions.toml: $name"
            )
        }
    }

    private fun findLibrary(name: String): String {
        try {
            return versionCatalog.findLibrary(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't find accessor in with-libraries.versions.toml: $name"
            )
        }
    }

    private fun findBundle(name: String): String {
        try {
            return versionCatalog.findBundle(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't find accessor in with-libraries.versions.toml: $name"
            )
        }
    }

    private fun findPlugin(name: String): String {
        try {
            return versionCatalog.findPlugin(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't find accessor in with-libraries.versions.toml: $name"
            )
        }
    }
}
