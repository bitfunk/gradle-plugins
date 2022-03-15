package com.example.catalog

import eu.bitfunk.gradle.version.catalog.VersionCatalogHelperContract
import eu.bitfunk.gradle.version.catalog.BaseVersionCatalogHelper
import kotlin.String
import org.gradle.api.Project

public class WithBundlesVersionCatalogHelper(
    project: Project,
    catalogName: String
) : BaseVersionCatalogHelper(project, catalogName) {
    public val versions: VersionCatalogHelperContract.Dependency.Group = object :
            VersionCatalogHelperContract.Dependency.Group {
    }

    public val bundles: VersionCatalogHelperContract.Dependency.Group = object :
            VersionCatalogHelperContract.Dependency.Group {
        public val example: VersionCatalogHelperContract.Dependency.Leaf = object :
                VersionCatalogHelperContract.Dependency.Leaf {
            public override fun `get`(): String = findBundle("example")
        }

        public val group: VersionCatalogHelperContract.Dependency.Group = object :
                VersionCatalogHelperContract.Dependency.Group {
            public val example: VersionCatalogHelperContract.Dependency.GroupLeaf = object :
                    VersionCatalogHelperContract.Dependency.GroupLeaf {
                public val one: VersionCatalogHelperContract.Dependency.Leaf = object :
                        VersionCatalogHelperContract.Dependency.Leaf {
                    public override fun `get`(): String = findBundle("group-example-one")
                }

                public val two: VersionCatalogHelperContract.Dependency.Leaf = object :
                        VersionCatalogHelperContract.Dependency.Leaf {
                    public override fun `get`(): String = findBundle("group-example-two")
                }

                public override fun `get`(): String = findBundle("group-example")
            }
        }
    }

    public val plugins: VersionCatalogHelperContract.Dependency.Group = object :
            VersionCatalogHelperContract.Dependency.Group {
    }
}
