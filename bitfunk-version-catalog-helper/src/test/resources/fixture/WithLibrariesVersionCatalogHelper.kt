package com.example.catalog

import eu.bitfunk.gradle.version.catalog.helper.BaseVersionCatalogHelper
import eu.bitfunk.gradle.version.catalog.helper.VersionCatalogDependency
import kotlin.String
import org.gradle.api.Project

public class WithLibrariesVersionCatalogHelper(
    project: Project,
    catalogName: String
) : BaseVersionCatalogHelper(project, catalogName) {
    public val versions: VersionCatalogDependency.Group = object : VersionCatalogDependency.Group {
    }

    public val bundles: VersionCatalogDependency.Group = object : VersionCatalogDependency.Group {
    }

    public val plugins: VersionCatalogDependency.Group = object : VersionCatalogDependency.Group {
    }

    public val example: VersionCatalogDependency.Leaf = object : VersionCatalogDependency.Leaf {
        public override fun `get`(): String = findLibrary("example")
    }

    public val group: VersionCatalogDependency.Group = object : VersionCatalogDependency.Group {
        public val example: VersionCatalogDependency.GroupLeaf = object :
                VersionCatalogDependency.GroupLeaf {
            public val one: VersionCatalogDependency.Leaf = object : VersionCatalogDependency.Leaf {
                public override fun `get`(): String = findLibrary("group-example-one")
            }

            public val two: VersionCatalogDependency.Leaf = object : VersionCatalogDependency.Leaf {
                public override fun `get`(): String = findLibrary("group-example-two")
            }

            public override fun `get`(): String = findLibrary("group-example")
        }
    }
}
