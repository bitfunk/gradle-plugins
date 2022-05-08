package com.example.catalog.generated

import com.example.catalog.generated.EmptyVersionCatalogAccessorContract.Bundles
import com.example.catalog.generated.EmptyVersionCatalogAccessorContract.Libraries
import com.example.catalog.generated.EmptyVersionCatalogAccessorContract.Plugins
import com.example.catalog.generated.EmptyVersionCatalogAccessorContract.Versions
import eu.bitfunk.gradle.plugin.version.catalog.accessor.BaseVersionCatalogAccessor
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
