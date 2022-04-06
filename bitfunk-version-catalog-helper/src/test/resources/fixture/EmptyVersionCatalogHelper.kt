package com.example.catalog

import eu.bitfunk.gradle.version.catalog.helper.BaseVersionCatalogHelper
import eu.bitfunk.gradle.version.catalog.helper.VersionCatalogDependency
import org.gradle.api.Project

public class EmptyVersionCatalogHelper(
    project: Project
) : BaseVersionCatalogHelper(project, "empty") {
    public val versions: VersionCatalogDependency.Group = object : VersionCatalogDependency.Group {
    }

    public val bundles: VersionCatalogDependency.Group = object : VersionCatalogDependency.Group {
    }

    public val plugins: VersionCatalogDependency.Group = object : VersionCatalogDependency.Group {
    }
}
