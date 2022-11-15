/*
 * ISC License
 *
 * Copyright (c) 2022. Wolf-Martell Montwé (bitfunk)
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
 * LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 * OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 * PERFORMANCE OF THIS SOFTWARE.
 */

package eu.bitfunk.gradle.plugin.development.convention.internal

import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar.Javadoc
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import eu.bitfunk.gradle.plugin.development.convention.GradlePluginConventionContract
import org.gradle.api.Action
import org.gradle.api.Project
import java.time.Year

@Suppress("UnstableApiUsage")
internal fun Project.mavenPublishing(action: Action<MavenPublishBaseExtension>) {
    extensions.configure(MavenPublishBaseExtension::class.java, action)
}

@Suppress("UnstableApiUsage")
internal fun configurePublishing(
    project: Project,
    extension: GradlePluginConventionContract.Extension
): Unit = with(project) {
    pluginManager.apply("com.vanniktech.maven.publish.base")

    mavenPublishing {
        publishToMavenCentral(SonatypeHost.S01)
        signAllPublications()
        configure(
            GradlePlugin(
                javadocJar = Javadoc(),
                sourcesJar = true
            )
        )

        pom {
            val gitHubOrg = extension.publishGitHubOrganization.get()
            val gitHubRepo = extension.publishGitHubRepositoryName.get()

            name.set(extension.publishName)
            description.set(extension.publishDescription)
            inceptionYear.set("${Year.now().value}")
            url.set(
                "https://github.com/$gitHubOrg/$gitHubRepo/"
            )

            licenses {
                license {
                    name.set("ISC License")
                    url.set("https://opensource.org/licenses/isc")
                    distribution.set("https://opensource.org/licenses/isc")
                }
            }

            developers {
                developer {
                    id.set("bitfunk")
                    name.set("Wolf-Martell Montwé (bitfunk)")
                    url.set("https://github.com/bitfunk/")
                }
            }

            scm {
                url.set(
                    "https://github.com/$gitHubOrg/$gitHubRepo/"
                )
                connection.set(
                    "scm:git:git://github.com/$gitHubOrg/$gitHubRepo.git"
                )
                developerConnection.set(
                    "scm:git:ssh://github.com/$gitHubOrg/$gitHubRepo.git"
                )
            }
        }
    }
}
