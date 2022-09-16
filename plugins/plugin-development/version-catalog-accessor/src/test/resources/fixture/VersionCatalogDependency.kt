package com.example.catalog.generated

import kotlin.String

public interface VersionCatalogDependency {
    public interface Group : VersionCatalogDependency

    public interface GroupLeaf : VersionCatalogDependency {
        public fun `get`(): String

        public fun getStatic(): String
    }

    public interface Leaf : VersionCatalogDependency {
        public fun `get`(): String

        public fun getStatic(): String
    }
}
