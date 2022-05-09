package eu.bitfunk.gradle.plugin.development.version.catalog.accessor.generated

import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.generated.TestVersionCatalogAccessorContract.Bundles
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.generated.TestVersionCatalogAccessorContract.Libraries
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.generated.TestVersionCatalogAccessorContract.Plugins
import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.generated.TestVersionCatalogAccessorContract.Versions
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import kotlin.String

public interface TestVersionCatalogAccessorContract {
    public interface Versions

    public interface Libraries

    public interface Bundles

    public interface Plugins
}

public class TestVersionCatalogAccessor(
    project: Project
) : Libraries {
    private val versionCatalog: VersionCatalog =
        project.extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

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
            throw NoSuchElementException("Can't find accessor in libs.versions.toml: $name")
        }
    }

    private fun findLibrary(name: String): String {
        try {
            return versionCatalog.findLibrary(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException("Can't find accessor in libs.versions.toml: $name")
        }
    }

    private fun findBundle(name: String): String {
        try {
            return versionCatalog.findBundle(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException("Can't find accessor in libs.versions.toml: $name")
        }
    }

    private fun findPlugin(name: String): String {
        try {
            return versionCatalog.findPlugin(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException("Can't find accessor in libs.versions.toml: $name")
        }
    }

    fun testFindVersion(name: String): String {
        return findVersion(name)
    }

    fun testFindLibrary(name: String): String {
        return findLibrary(name)
    }

    fun testFindBundle(name: String): String {
        return findBundle(name)
    }

    fun testFindPlugin(name: String): String {
        return findPlugin(name)
    }
}
