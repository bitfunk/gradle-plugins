package com.example.catalog

import com.example.catalog.WithBundlesVersionCatalogAccessorContract.Bundles
import com.example.catalog.WithBundlesVersionCatalogAccessorContract.Libraries
import com.example.catalog.WithBundlesVersionCatalogAccessorContract.Plugins
import com.example.catalog.WithBundlesVersionCatalogAccessorContract.Versions
import eu.bitfunk.gradle.plugin.version.catalog.accessor.BaseVersionCatalogAccessor
import eu.bitfunk.gradle.plugin.version.catalog.accessor.VersionCatalogDependency
import kotlin.String
import org.gradle.api.Project

public interface WithBundlesVersionCatalogAccessorContract {
    public interface Versions

    public interface Libraries

    public interface Bundles {
        public val example: VersionCatalogDependency.Leaf

        public val group: Group

        public interface Group : VersionCatalogDependency.Group {
            public val example: Example

            public interface Example : VersionCatalogDependency.GroupLeaf {
                public val one: VersionCatalogDependency.Leaf

                public val two: VersionCatalogDependency.Leaf
            }
        }
    }

    public interface Plugins
}

public class WithBundlesVersionCatalogAccessor(
    project: Project
) : BaseVersionCatalogAccessor(project, "with-bundles"), Libraries {
    public val versions: Versions = object : Versions {
    }

    public val bundles: Bundles = object : Bundles {
        public override val example: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findBundle("example")
        }

        public override val group: Bundles.Group = object : Bundles.Group {
            public override val example: Bundles.Group.Example = object : Bundles.Group.Example {
                public override val one: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String = findBundle("group-example-one")
                }

                public override val two: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String = findBundle("group-example-two")
                }

                public override fun `get`(): String = findBundle("group-example")
            }
        }
    }

    public val plugins: Plugins = object : Plugins {
    }
}
