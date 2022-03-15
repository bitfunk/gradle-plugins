package com.example.catalog

import eu.bitfunk.gradle.version.catalog.BaseVersionCatalogHelper
import eu.bitfunk.gradle.version.catalog.Dependency
import kotlin.String
import org.gradle.api.Project

public class EmptyVersionCatalogHelper(
    project: Project,
    catalogName: String
) : BaseVersionCatalogHelper(project, catalogName) {
    public val versions: VersionCatalogHelperContract.Dependency.Group = object :
            VersionCatalogHelperContract.Dependency.Group {
    }

    public val bundles: VersionCatalogHelperContract.Dependency.Group = object :
            VersionCatalogHelperContract.Dependency.Group {
    }

    public val plugins: VersionCatalogHelperContract.Dependency.Group = object :
            VersionCatalogHelperContract.Dependency.Group {
    }
}
