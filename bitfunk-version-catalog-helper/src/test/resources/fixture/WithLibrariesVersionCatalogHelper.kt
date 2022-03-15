package com.example.catalog

import eu.bitfunk.gradle.version.catalog.VersionCatalogHelperContract
import eu.bitfunk.gradle.version.catalog.intern.BaseVersionCatalogHelper
import kotlin.String
import org.gradle.api.Project

public class WithLibrariesVersionCatalogHelper(
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

    public val example: VersionCatalogHelperContract.Dependency.Leaf = object :
            VersionCatalogHelperContract.Dependency.Leaf {
        public override fun `get`(): String = findLibrary("example")
    }

    public val group: VersionCatalogHelperContract.Dependency.Group = object :
            VersionCatalogHelperContract.Dependency.Group {
        public val example: VersionCatalogHelperContract.Dependency.GroupLeaf = object :
                VersionCatalogHelperContract.Dependency.GroupLeaf {
            public val one: VersionCatalogHelperContract.Dependency.Leaf = object :
                    VersionCatalogHelperContract.Dependency.Leaf {
                public override fun `get`(): String = findLibrary("group-example-one")
            }

            public val two: VersionCatalogHelperContract.Dependency.Leaf = object :
                    VersionCatalogHelperContract.Dependency.Leaf {
                public override fun `get`(): String = findLibrary("group-example-two")
            }

            public override fun `get`(): String = findLibrary("group-example")
        }
    }
}