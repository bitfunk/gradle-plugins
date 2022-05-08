package com.example.catalog.generated

import com.example.catalog.generated.WithPluginsVersionCatalogAccessorContract.Bundles
import com.example.catalog.generated.WithPluginsVersionCatalogAccessorContract.Libraries
import com.example.catalog.generated.WithPluginsVersionCatalogAccessorContract.Plugins
import com.example.catalog.generated.WithPluginsVersionCatalogAccessorContract.Versions
import eu.bitfunk.gradle.plugin.version.catalog.accessor.BaseVersionCatalogAccessor
import eu.bitfunk.gradle.plugin.version.catalog.accessor.VersionCatalogDependency
import kotlin.String
import org.gradle.api.Project

public interface WithPluginsVersionCatalogAccessorContract {
    public interface Versions

    public interface Libraries

    public interface Bundles

    public interface Plugins {
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
}

public class WithPluginsVersionCatalogAccessor(
    project: Project
) : BaseVersionCatalogAccessor(project, "with-plugins"), Libraries {
    public val versions: Versions = object : Versions {
    }

    public val bundles: Bundles = object : Bundles {
    }

    public val plugins: Plugins = object : Plugins {
        public override val example: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findPlugin("example")
        }

        public override val group: Plugins.Group = object : Plugins.Group {
            public override val example: Plugins.Group.Example = object : Plugins.Group.Example {
                public override val one: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String = findPlugin("group-example-one")
                }

                public override val two: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String = findPlugin("group-example-two")
                }

                public override fun `get`(): String = findPlugin("group-example")
            }
        }
    }
}
