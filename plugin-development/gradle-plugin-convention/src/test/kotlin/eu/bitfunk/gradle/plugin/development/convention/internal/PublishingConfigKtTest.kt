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
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import eu.bitfunk.gradle.plugin.development.convention.GradlePluginConventionPluginExtension
import eu.bitfunk.gradle.plugin.development.test.util.stubGradleAction
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyAll
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPomDeveloper
import org.gradle.api.publish.maven.MavenPomDeveloperSpec
import org.gradle.api.publish.maven.MavenPomLicense
import org.gradle.api.publish.maven.MavenPomLicenseSpec
import org.gradle.api.publish.maven.MavenPomScm
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Year

@Suppress("UnstableApiUsage")
internal class PublishingConfigKtTest {

    private lateinit var extension: GradlePluginConventionPluginExtension

    private lateinit var project: Project
    private lateinit var pluginManager: PluginManager
    private lateinit var extensionContainer: ExtensionContainer
    private lateinit var mavenPublishBaseExtension: MavenPublishBaseExtension

    @BeforeEach
    fun setup() {
        extension = mockk(relaxed = true)

        project = mockk()
        pluginManager = mockk(relaxed = true)
        extensionContainer = mockk()
        mavenPublishBaseExtension = mockk(relaxed = true)

        every { project.pluginManager } returns pluginManager
        every { project.extensions } returns extensionContainer
        every { project.group } returns "group"
        every { project.version } returns "version"
        stubGradleAction(mavenPublishBaseExtension) {
            extensionContainer.configure(MavenPublishBaseExtension::class.java, it)
        }
        every { extension.projectGitHubOrganization.get() } returns "ORG_NAME"
        every { extension.projectGitHubRepositoryName.get() } returns "REPOSITORY_NAME"
    }

    @Test
    fun `GIVEN project WHEN configurePublishing() THEN all plugins present`() {
        // GIVEN

        // WHEN
        configurePublishing(project, extension)

        // THEN
        verify {
            pluginManager.apply("com.vanniktech.maven.publish.base")
        }

        confirmVerified(pluginManager)
    }

    @Test
    fun `GIVEN project WHEN configurePublishing() THEN publishing configured`() {
        // GIVEN
        val pom: MavenPom = mockk(relaxed = true)
        stubGradleAction(pom) { mavenPublishBaseExtension.pom(it) }

        // WHEN
        configurePublishing(project, extension)

        // THEN
        verifyAll {
            project.pluginManager
            project.extensions

            extensionContainer.configure(MavenPublishBaseExtension::class.java, any())

            mavenPublishBaseExtension.publishToMavenCentral(SonatypeHost.S01)
            mavenPublishBaseExtension.signAllPublications()
            mavenPublishBaseExtension.configure(
                GradlePlugin(
                    javadocJar = JavadocJar.Javadoc(),
                    sourcesJar = true
                )
            )

            mavenPublishBaseExtension.pom(any())

            extension.projectName
            extension.projectDescription
            extension.projectGitHubOrganization
            extension.projectGitHubRepositoryName
        }

        confirmVerified(project, extension, extensionContainer, mavenPublishBaseExtension)
    }

    @Test
    fun `GIVEN project WHEN configurePublishing() THEN publishing pom configured`() {
        // GIVEN
        val pom: MavenPom = mockk(relaxed = true)
        val nameProperty: Property<String> = mockk(relaxed = true)
        val descriptionProperty: Property<String> = mockk(relaxed = true)
        val yearProperty: Property<String> = mockk(relaxed = true)
        val urlProperty: Property<String> = mockk(relaxed = true)
        val pomLicenseSpec: MavenPomLicenseSpec = mockk(relaxed = true)
        val pomLicense: MavenPomLicense = mockk(relaxed = true)
        val pomDeveloperSpec: MavenPomDeveloperSpec = mockk(relaxed = true)
        val pomDeveloper: MavenPomDeveloper = mockk(relaxed = true)
        val pomScm: MavenPomScm = mockk(relaxed = true)
        stubGradleAction(pom) { mavenPublishBaseExtension.pom(it) }
        every { pom.name } returns nameProperty
        every { pom.description } returns descriptionProperty
        every { pom.inceptionYear } returns yearProperty
        every { pom.url } returns urlProperty
        stubGradleAction(pomLicenseSpec) { pom.licenses(it) }
        stubGradleAction(pomLicense) { pomLicenseSpec.license(it) }
        stubGradleAction(pomDeveloperSpec) { pom.developers(it) }
        stubGradleAction(pomDeveloper) { pomDeveloperSpec.developer(it) }
        stubGradleAction(pomScm) { pom.scm(it) }

        // WHEN
        configurePublishing(project, extension)

        // THEN
        verifyAll {
            pom.name
            pom.description
            pom.inceptionYear
            pom.url
            pom.licenses(any())
            pom.developers(any())
            pom.scm(any())

            extension.projectName
            extension.projectDescription
            extension.projectGitHubOrganization
            extension.projectGitHubRepositoryName

            nameProperty.set(extension.projectName)
            descriptionProperty.set(extension.projectDescription)
            yearProperty.set("${Year.now().value}")
            urlProperty.set("https://github.com/ORG_NAME/REPOSITORY_NAME/")

            pomLicenseSpec.license(any())

            pomLicense.name.set("ISC License")
            pomLicense.url.set("https://opensource.org/licenses/isc")
            pomLicense.distribution.set("https://opensource.org/licenses/isc")

            pomDeveloperSpec.developer(any())
            pomDeveloper.id.set("bitfunk")
            pomDeveloper.name.set("Wolf-Martell Montwé (bitfunk)")
            pomDeveloper.url.set("https://github.com/bitfunk/")

            pomScm.url.set("https://github.com/ORG_NAME/REPOSITORY_NAME/")
            pomScm.connection.set("scm:git:git://github.com/ORG_NAME/REPOSITORY_NAME.git")
            pomScm.developerConnection.set("scm:git:ssh://github.com/ORG_NAME/REPOSITORY_NAME.git")
        }

        confirmVerified(
            pom,
            nameProperty,
            descriptionProperty,
            yearProperty,
            urlProperty,
            pomLicenseSpec,
            pomLicense,
            pomDeveloperSpec,
            pomDeveloper,
            pomScm
        )
    }
}
