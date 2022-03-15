package com.example.catalog

import eu.bitfunk.gradle.version.catalog.helper.BaseVersionCatalogHelper
import eu.bitfunk.gradle.version.catalog.helper.Dependency
import kotlin.String
import org.gradle.api.Project

public class EmptyVersionCatalogHelper(
    project: Project,
    catalogName: String
) : BaseVersionCatalogHelper(project, catalogName) {
    public val versions: Dependency.Group = object : Dependency.Group {
    }

    public val bundles: Dependency.Group = object : Dependency.Group {
    }

    public val plugins: Dependency.Group = object : Dependency.Group {
    }
}
