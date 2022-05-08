package com.example.catalog.generated

import com.example.catalog.generated.WithVersionsVersionCatalogAccessorContract.Bundles
import com.example.catalog.generated.WithVersionsVersionCatalogAccessorContract.Libraries
import com.example.catalog.generated.WithVersionsVersionCatalogAccessorContract.Plugins
import com.example.catalog.generated.WithVersionsVersionCatalogAccessorContract.Versions
import eu.bitfunk.gradle.plugin.version.catalog.accessor.BaseVersionCatalogAccessor
import eu.bitfunk.gradle.plugin.version.catalog.accessor.VersionCatalogDependency
import kotlin.String
import org.gradle.api.Project

public interface WithVersionsVersionCatalogAccessorContract {
    public interface Versions {
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

    public interface Libraries

    public interface Bundles

    public interface Plugins
}

public class WithVersionsVersionCatalogAccessor(
    project: Project
) : BaseVersionCatalogAccessor(project, "with-versions"), Libraries {
    public val versions: Versions = object : Versions {
        public override val example: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findVersion("example")
        }

        public override val group: Versions.Group = object : Versions.Group {
            public override val example: Versions.Group.Example = object : Versions.Group.Example {
                public override val one: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String = findVersion("group-example-one")
                }

                public override val two: VersionCatalogDependency.Leaf = object :
                        VersionCatalogDependency.Leaf {
                    public override fun `get`(): String = findVersion("group-example-two")
                }

                public override fun `get`(): String = findVersion("group-example")
            }
        }
    }

    public val bundles: Bundles = object : Bundles {
    }

    public val plugins: Plugins = object : Plugins {
    }
}
