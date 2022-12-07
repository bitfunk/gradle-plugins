package eu.bitfunk.gradle.plugin.quality.code.analysis.libs.generated

import eu.bitfunk.gradle.plugin.quality.code.analysis.libs.generated.LibsCodeAnalysisVersionCatalogAccessorContract.Bundles
import eu.bitfunk.gradle.plugin.quality.code.analysis.libs.generated.LibsCodeAnalysisVersionCatalogAccessorContract.Libraries
import eu.bitfunk.gradle.plugin.quality.code.analysis.libs.generated.LibsCodeAnalysisVersionCatalogAccessorContract.Plugins
import eu.bitfunk.gradle.plugin.quality.code.analysis.libs.generated.LibsCodeAnalysisVersionCatalogAccessorContract.Versions
import kotlin.String
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

public interface LibsCodeAnalysisVersionCatalogAccessorContract {
    public interface Versions {
        public val detekt: VersionCatalogDependency.Leaf

        public val gradlePluginConvention: VersionCatalogDependency.Leaf

        public val gradleVersionCatalogAccessor: VersionCatalogDependency.Leaf
    }

    public interface Libraries {
        public val gradleDetektPlugin: VersionCatalogDependency.Leaf
    }

    public interface Bundles

    public interface Plugins {
        public val gradlePluginConvention: VersionCatalogDependency.Leaf

        public val gradleVersionCatalogAccessor: VersionCatalogDependency.Leaf
    }
}

public class LibsCodeAnalysisVersionCatalogAccessor(
    private val project: Project,
) : Libraries {
    private val versionCatalog: VersionCatalog
        get() = project.extensions.getByType(VersionCatalogsExtension::class.java)
            .named("libs-code-analysis")

    public val versions: Versions = object : Versions {
        public override val detekt: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findVersion("detekt")

            public override fun getStatic(): String = "1.22.0"
        }

        public override val gradlePluginConvention: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findVersion("gradlePluginConvention")

            public override fun getStatic(): String = "0.0.7"
        }

        public override val gradleVersionCatalogAccessor: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findVersion("gradleVersionCatalogAccessor")

            public override fun getStatic(): String = "0.1.0"
        }
    }

    public val bundles: Bundles = object : Bundles {
    }

    public val plugins: Plugins = object : Plugins {
        public override val gradlePluginConvention: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findPlugin("gradlePluginConvention")

            public override fun getStatic(): String = throw UnsupportedOperationException(
                "not yet implemented"
            )
        }

        public override val gradleVersionCatalogAccessor: VersionCatalogDependency.Leaf = object :
                VersionCatalogDependency.Leaf {
            public override fun `get`(): String = findPlugin("gradleVersionCatalogAccessor")

            public override fun getStatic(): String = throw UnsupportedOperationException(
                "not yet implemented"
            )
        }
    }

    public override val gradleDetektPlugin: VersionCatalogDependency.Leaf = object :
            VersionCatalogDependency.Leaf {
        public override fun `get`(): String = findLibrary("gradleDetektPlugin")

        public override fun getStatic(): String = throw UnsupportedOperationException(
            "not yet implemented"
        )
    }

    private fun findVersion(name: String): String {
        try {
            return versionCatalog.findVersion(name).get().requiredVersion
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't findVersion accessor in Libs-code-analysis.versions.toml: $name"
            )
        }
    }

    private fun findLibrary(name: String): String {
        try {
            return versionCatalog.findLibrary(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't findLibrary accessor in Libs-code-analysis.versions.toml: $name"
            )
        }
    }

    private fun findBundle(name: String): String {
        try {
            return versionCatalog.findBundle(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't findBundle accessor in Libs-code-analysis.versions.toml: $name"
            )
        }
    }

    private fun findPlugin(name: String): String {
        try {
            return versionCatalog.findPlugin(name).get().get().toString()
        } catch (error: Throwable) {
            throw NoSuchElementException(
                "Can't findPlugin accessor in Libs-code-analysis.versions.toml: $name"
            )
        }
    }
}
