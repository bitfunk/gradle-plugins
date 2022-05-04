package com.example.catalog

import eu.bitfunk.gradle.plugin.version.catalog.accessor.BaseVersionCatalogAccessor
import eu.bitfunk.gradle.plugin.version.catalog.accessor.VersionCatalogDependency
import kotlin.String
import org.gradle.api.Project

public class WithVersionsVersionCatalogAccessor(
    project: Project
) : BaseVersionCatalogAccessor(project, "with-versions") {
    public val versions: VersionCatalogDependency.Group = object : VersionCatalogDependency.Group {
        public val example: VersionCatalogDependency.Leaf = object : VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findVersion("example")
        }

        public val group: VersionCatalogDependency.Group = object : VersionCatalogDependency.Group {
            public val example: VersionCatalogDependency.GroupLeaf = object :
                    VersionCatalogDependency.GroupLeaf {
                public val one: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String = findVersion("group-example-one")
                }

                public val two: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String = findVersion("group-example-two")
                }

                public override fun `get`(): String = findVersion("group-example")
            }
        }
    }

    public val bundles: VersionCatalogDependency.Group = object : VersionCatalogDependency.Group {
    }

    public val plugins: VersionCatalogDependency.Group = object : VersionCatalogDependency.Group {
    }
}
