package com.example.catalog

import com.example.catalog.EmptyVersionCatalogAccessorContract.Bundles
import com.example.catalog.EmptyVersionCatalogAccessorContract.Libraries
import com.example.catalog.EmptyVersionCatalogAccessorContract.Plugins
import com.example.catalog.EmptyVersionCatalogAccessorContract.Versions
import eu.bitfunk.gradle.plugin.version.catalog.accessor.BaseVersionCatalogAccessor
import eu.bitfunk.gradle.plugin.version.catalog.accessor.VersionCatalogDependency
import org.gradle.api.Project

public interface EmptyVersionCatalogAccessorContract {
    public interface Versions

    public interface Libraries

    public interface Bundles

    public interface Plugins
}

public class EmptyVersionCatalogAccessor(
    project: Project
) : BaseVersionCatalogAccessor(project, "empty"), Libraries {
    public val versions: Versions = object : Versions {
    }

    public val bundles: Bundles = object : Bundles {
    }

    public val plugins: Plugins = object : Plugins {
    }
}
