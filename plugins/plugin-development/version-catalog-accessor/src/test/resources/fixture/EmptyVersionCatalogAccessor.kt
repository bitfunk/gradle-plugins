package com.example.catalog.generated

import com.example.catalog.generated.EmptyVersionCatalogAccessorContract.Bundles
import com.example.catalog.generated.EmptyVersionCatalogAccessorContract.Libraries
import com.example.catalog.generated.EmptyVersionCatalogAccessorContract.Plugins
import com.example.catalog.generated.EmptyVersionCatalogAccessorContract.Versions
import kotlin.String
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

public interface EmptyVersionCatalogAccessorContract {
    public interface Versions

    public interface Libraries

    public interface Bundles

    public interface Plugins
}

public class EmptyVersionCatalogAccessor(
    project: Project
) : Libraries {
    private val versionCatalog: VersionCatalog =
            project.extensions.getByType(VersionCatalogsExtension::class.java).named("empty")

    public val versions: Versions = object : Versions {
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
                "Can't find accessor in empty.versions.toml: $name"
            )
        }
    }

    private fun findLibrary(name: String): String {
        try {
            return versionCatalog.findLibrary(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't find accessor in empty.versions.toml: $name"
            )
        }
    }

    private fun findBundle(name: String): String {
        try {
            return versionCatalog.findBundle(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't find accessor in empty.versions.toml: $name"
            )
        }
    }

    private fun findPlugin(name: String): String {
        try {
            return versionCatalog.findPlugin(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't find accessor in empty.versions.toml: $name"
            )
        }
    }
}
