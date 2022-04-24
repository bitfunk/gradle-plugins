package com.example.catalog

import eu.bitfunk.gradle.version.catalog.accessor.BaseVersionCatalogAccessor
import eu.bitfunk.gradle.version.catalog.accessor.VersionCatalogDependency
import kotlin.String
import org.gradle.api.Project

public class WithPluginsVersionCatalogAccessor(
    project: Project
) : BaseVersionCatalogAccessor(project, "with-plugins") {
    public val versions: VersionCatalogDependency.Group = object : VersionCatalogDependency.Group {
    }

    public val bundles: VersionCatalogDependency.Group = object : VersionCatalogDependency.Group {
    }

    public val plugins: VersionCatalogDependency.Group = object : VersionCatalogDependency.Group {
        public val example: VersionCatalogDependency.Leaf = object : VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findPlugin("example")
        }

        public val group: VersionCatalogDependency.Group = object : VersionCatalogDependency.Group {
            public val example: VersionCatalogDependency.GroupLeaf = object :
                    VersionCatalogDependency.GroupLeaf {
                public val one: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String = findPlugin("group-example-one")
                }

                public val two: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String = findPlugin("group-example-two")
                }

                public override fun `get`(): String = findPlugin("group-example")
            }
        }
    }
}
