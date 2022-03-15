package com.example.catalog

import eu.bitfunk.gradle.version.catalog.helper.BaseVersionCatalogHelper
import eu.bitfunk.gradle.version.catalog.helper.Dependency
import kotlin.String
import org.gradle.api.Project

public class WithVersionsVersionCatalogHelper(
    project: Project,
    catalogName: String
) : BaseVersionCatalogHelper(project, catalogName) {
    public val versions: Dependency.Group = object : Dependency.Group {
        public val example: Dependency.Leaf = object : Dependency.Leaf {
            public override fun `get`(): String = findVersion("example")
        }

        public val group: Dependency.Group = object : Dependency.Group {
            public val example: Dependency.GroupLeaf = object : Dependency.GroupLeaf {
                public val one: Dependency.Leaf = object : Dependency.Leaf {
                    public override fun `get`(): String = findVersion("group-example-one")
                }

                public val two: Dependency.Leaf = object : Dependency.Leaf {
                    public override fun `get`(): String = findVersion("group-example-two")
                }

                public override fun `get`(): String = findVersion("group-example")
            }
        }
    }

    public val bundles: Dependency.Group = object : Dependency.Group {
    }

    public val plugins: Dependency.Group = object : Dependency.Group {
    }
}
