package com.example.catalog.generated

import com.example.catalog.generated.WithLibrariesVersionCatalogAccessorContract.Bundles
import com.example.catalog.generated.WithLibrariesVersionCatalogAccessorContract.Libraries
import com.example.catalog.generated.WithLibrariesVersionCatalogAccessorContract.Plugins
import com.example.catalog.generated.WithLibrariesVersionCatalogAccessorContract.Versions
import eu.bitfunk.gradle.plugin.version.catalog.accessor.BaseVersionCatalogAccessor
import eu.bitfunk.gradle.plugin.version.catalog.accessor.VersionCatalogDependency
import kotlin.String
import org.gradle.api.Project

public interface WithLibrariesVersionCatalogAccessorContract {
    public interface Versions

    public interface Libraries {
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

    public interface Bundles

    public interface Plugins
}

public class WithLibrariesVersionCatalogAccessor(
    project: Project
) : BaseVersionCatalogAccessor(project, "with-libraries"), Libraries {
    public val versions: Versions = object : Versions {
    }

    public val bundles: Bundles = object : Bundles {
    }

    public val plugins: Plugins = object : Plugins {
    }

    public override val example: VersionCatalogDependency.Leaf = object :
            VersionCatalogDependency.Leaf {
        public override fun `get`(): String = findLibrary("example")
    }

    public override val group: Libraries.Group = object : Libraries.Group {
        public override val example: Libraries.Group.Example = object : Libraries.Group.Example {
            public override val one: VersionCatalogDependency.Leaf = object :
                    VersionCatalogDependency.Leaf {
                public override fun `get`(): String = findLibrary("group-example-one")
            }

            public override val two: VersionCatalogDependency.Leaf = object :
                    VersionCatalogDependency.Leaf {
                public override fun `get`(): String = findLibrary("group-example-two")
            }

            public override fun `get`(): String = findLibrary("group-example")
        }
    }
}
