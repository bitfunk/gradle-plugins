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

package eu.bitfunk.gradle.plugin.tool.publish

import eu.bitfunk.gradle.plugin.tool.publish.PublishContract.Companion.EXTENSION_NAME
import eu.bitfunk.gradle.plugin.tool.publish.PublishContract.Extension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension

public class PublishPlugin : PublishContract.Plugin, Plugin<Project> {

    override fun apply(target: Project) {
        addPlugins(target)
        val extension = addExtension(target)
        configurePublishing(target, extension)
        configureSigning(target, extension)
    }

    override fun addPlugins(project: Project) {
        project.pluginManager.apply("org.gradle.maven-publish")
        project.pluginManager.apply("org.gradle.signing")
    }

    override fun addExtension(project: Project): Extension = with(project) {
        val extension = extensions.create(EXTENSION_NAME, PublishPluginExtension::class.java)

        extension.signingEnabled.convention(false)

        return extension
    }

    override fun configurePublishing(project: Project, extension: Extension): Unit = with(project) {
        publishing {
            publications {
                // for (component in components) {
                //     create<MavenPublication>(component.name) {
                //         from(this@)
                //     }
                // }

                withType<MavenPublication> {
                    // artifacts.forEach(::artifact)

                    pom {
                        name.set(extension.projectName)
                        description.set(extension.projectDescription)
                        url.set(extension.projectUrl)

                        licenses {
                            license {
                                name.set(extension.licenseName)
                                url.set(extension.licenseUrl)
                            }
                        }

                        developers {
                            developer {
                                name.set(extension.developerName)
                                email.set(extension.developerEmail)
                                organization.set(extension.organizationName)
                                organizationUrl.set(extension.organizationUrl)
                            }
                        }

                        scm {
                            val scmUrl = extension.scmUrl.get()
                            val scmConnection = scmUrl.substringAfter("//").substringBefore("/tree/")
                            val scmDeveloperConnection = scmConnection.replaceFirst("/", ":")

                            connection.set("scm:git:git://$scmConnection.git")
                            developerConnection.set("scm:git:ssh://$scmDeveloperConnection.git")
                            url.set(extension.scmUrl)
                        }
                    }
                }
            }
        }
    }

    override fun configureSigning(project: Project, extension: Extension): Unit = with(project) {
        if (extension.signingEnabled.get()) {
            val publications = publishingExtension().publications

            signing {
                useInMemoryPgpKeys(
                    loadSigningKey(project),
                    loadSigningPassword(project)
                )

                sign(publications)
            }
        }
    }

    private fun loadSigningKey(project: Project): String {
        return loadProperty(project, SIGNING_KEY_PROPERTY_NAME, SIGNING_KEY_ENV_NAME)
    }

    private fun loadSigningPassword(project: Project): String {
        return loadProperty(project, SIGNING_PASSWORD_PROPERTY_NAME, SIGNING_PASSWORD_ENV_NAME)
    }

    private fun loadProperty(project: Project, propertyName: String, envName: String): String {
        val property = project.findProperty(propertyName) as String? ?: System.getenv(envName)

        if (property.isNullOrBlank()) {
            throw IllegalArgumentException("Can't find gradle property $propertyName or system env $envName")
        }

        return property
    }

    private fun Project.publishing(action: Action<PublishingExtension>) {
        extensions.configure(PublishingExtension::class.java, action)
    }

    private fun Project.signing(action: Action<SigningExtension>) {
        extensions.configure(SigningExtension::class.java, action)
    }

    private fun Project.publishingExtension(): PublishingExtension {
        return extensions.getByName<PublishingExtension>("publishing")
    }

    private companion object {
        private const val SIGNING_KEY_PROPERTY_NAME = "signing.key"
        private const val SIGNING_PASSWORD_PROPERTY_NAME = "signing.password"

        private const val SIGNING_KEY_ENV_NAME = "SIGNING_KEY"
        private const val SIGNING_PASSWORD_ENV_NAME = "SIGNING_PASSWORD"
    }
}
