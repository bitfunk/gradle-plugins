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

import eu.bitfunk.gradle.plugin.development.test.util.stubGradleAction
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verifyAll
import io.mockk.verifyOrder
import org.gradle.api.Action
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPomDeveloper
import org.gradle.api.publish.maven.MavenPomDeveloperSpec
import org.gradle.api.publish.maven.MavenPomIssueManagement
import org.gradle.api.publish.maven.MavenPomLicense
import org.gradle.api.publish.maven.MavenPomLicenseSpec
import org.gradle.api.publish.maven.MavenPomScm
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PublishPluginTest {

    private lateinit var testSubject: PublishPlugin

    @BeforeEach
    fun setup() {
        testSubject = PublishPlugin()
    }

    @Test
    fun `implements contract`() {
        assertInstanceOf(
            PublishContract.Plugin::class.java,
            testSubject,
        )
    }

    @Test
    fun `implements plugin`() {
        assertInstanceOf(
            Plugin::class.java,
            testSubject,
        )
    }

    @Test
    fun `GIVEN project WHEN addPlugins() THEN plugins added`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)

        // WHEN
        testSubject.addPlugins(project)

        // THEN
        verifyAll {
            project.pluginManager.apply("org.gradle.maven-publish")
            project.pluginManager.apply("org.gradle.signing")
        }

        confirmVerified(project)
    }

    @Test
    fun `GIVEN project WHEN addExtension() THEN extension added`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)
        val extension: PublishPluginExtension = mockk(relaxed = true)
        every { project.extensions.create("publishConfig", PublishPluginExtension::class.java) } returns extension

        // WHEN
        testSubject.addExtension(project)

        // THEN
        verifyAll {
            project.extensions.create("publishConfig", PublishPluginExtension::class.java)

            extension.signingEnabled.convention(false)
        }

        confirmVerified(project, extension)
    }

    @Test
    fun `GIVEN project WHEN configurePublishing() THEN publishing configured`() {
        // GIVEN
        val project: Project = mockk(relaxed = false)
        val extension: PublishPluginExtension = mockk(relaxed = false)
        val publishingExtension: PublishingExtension = mockk(relaxed = false)
        val publicationContainer: PublicationContainer = mockk()
        val domainObjectCollection: DomainObjectCollection<MavenPublication> = mockk()
        val mavenPublication: MavenPublication = mockk()
        val mavenPom: MavenPom = mockk(relaxed = true)

        stubGradleAction(publishingExtension) {
            project.extensions.configure(PublishingExtension::class.java, it)
        }
        stubGradleAction(publicationContainer) { publishingExtension.publications(it) }
        every {
            publicationContainer.withType(MavenPublication::class.java, any<Action<MavenPublication>>())
        } answers {
            secondArg<Action<MavenPublication>>().execute(mavenPublication)
            domainObjectCollection
        }
        stubGradleAction(mavenPom) { mavenPublication.pom(it) }

        val extensionSetup = setupExtension(extension)
        extensionSetup.first()

        val pomSetup = setupPom(mavenPom)
        pomSetup.first()

        // WHEN
        testSubject.configurePublishing(project, extension)

        extensionSetup.second()
        pomSetup.second()

        // THEN
        verifyAll {
            project.extensions.configure(PublishingExtension::class.java, any())

            publishingExtension.publications(any())
            publicationContainer.withType(MavenPublication::class.java, any<Action<MavenPublication>>())

            mavenPublication.pom(any())
        }

        pomSetup.second

        confirmVerified(
            project,
            publishingExtension,
            publicationContainer,
            domainObjectCollection,
            mavenPublication,
        )
    }

    private fun setupExtension(extension: PublishPluginExtension): Pair<() -> Unit, () -> Unit> {
        val setup: () -> Unit = {
            every { extension.projectName } returns mockk()
            every { extension.projectDescription } returns mockk()
            every { extension.projectUrl } returns mockk()

            every { extension.licenseName } returns mockk()
            every { extension.licenseUrl } returns mockk()

            every { extension.developerId } returns mockk()
            every { extension.developerName } returns mockk()
            every { extension.developerEmail } returns mockk()

            every { extension.organizationName } returns mockk()
            every { extension.organizationUrl } returns mockk()

            every { extension.scmUrl.get() } returns "https://github.com/bitfunk/gradle-plugins/tree/main"

            every { extension.issueManagement } returns mockk()
            every { extension.issueUrl } returns mockk()
        }

        val verify: () -> Unit = {
            verifyAll {
                extension.projectName
                extension.projectDescription
                extension.projectUrl

                extension.licenseName
                extension.licenseUrl

                extension.developerId
                extension.developerName
                extension.developerEmail

                extension.organizationName
                extension.organizationUrl

                extension.scmUrl.get()

                extension.issueManagement
                extension.issueUrl
            }

            confirmVerified(extension)
        }

        return Pair(setup, verify)
    }

    private fun setupPom(mavenPom: MavenPom): Pair<() -> Unit, () -> Unit> {
        val mavenPomLicenseSpec: MavenPomLicenseSpec = mockk(relaxed = false)
        val mavenPomLicense: MavenPomLicense = mockk(relaxed = true)
        val mavenPomDeveloperSpec: MavenPomDeveloperSpec = mockk(relaxed = false)
        val mavenPomDeveloper: MavenPomDeveloper = mockk(relaxed = true)
        val mavenPomScm: MavenPomScm = mockk(relaxed = true)
        val mavenPomIssueManagement: MavenPomIssueManagement = mockk(relaxed = true)

        val setup: () -> Unit = {
            stubGradleAction(mavenPomLicenseSpec) { mavenPom.licenses(it) }
            stubGradleAction(mavenPomLicense) { mavenPomLicenseSpec.license(it) }
            stubGradleAction(mavenPomDeveloperSpec) { mavenPom.developers(it) }
            stubGradleAction(mavenPomDeveloper) { mavenPomDeveloperSpec.developer(it) }
            stubGradleAction(mavenPomScm) { mavenPom.scm(it) }

            val mavenPomScmUrlProperty: Property<String> = mockk(relaxed = true)
            every { mavenPomScm.url } returns mavenPomScmUrlProperty

            stubGradleAction(mavenPomIssueManagement) { mavenPom.issueManagement(it) }
        }

        val verify: () -> Unit = {
            verifyAll {
                mavenPom.name
                mavenPom.description
                mavenPom.url

                mavenPom.licenses(any())
                mavenPomLicenseSpec.license(any())
                mavenPomLicense.name
                mavenPomLicense.url

                mavenPom.developers(any())
                mavenPomDeveloperSpec.developer(any())
                mavenPomDeveloper.id
                mavenPomDeveloper.name
                mavenPomDeveloper.email
                mavenPomDeveloper.organization
                mavenPomDeveloper.organizationUrl

                mavenPom.scm(any())
                mavenPomScm.connection
                mavenPomScm.developerConnection
                mavenPomScm.url

                mavenPom.issueManagement(any())
                mavenPomIssueManagement.system
                mavenPomIssueManagement.url
            }

            confirmVerified(
                mavenPom,
                mavenPomLicenseSpec,
                mavenPomLicense,
                mavenPomDeveloperSpec,
                mavenPomDeveloper,
                mavenPomScm,
                mavenPomIssueManagement,
            )
        }

        return Pair(setup, verify)
    }

    @Test
    fun `GIVEN project and signing is disabled WHEN configureSigning() THEN nothing configured`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)
        val extension: PublishPluginExtension = mockk(relaxed = true)
        every { extension.signingEnabled.get() } returns false

        // WHEN
        testSubject.configureSigning(project, extension)

        // THEN
        verifyAll {
            extension.signingEnabled.get()
        }

        confirmVerified(project, extension)
    }

    @Test
    fun `GIVEN project without properties WHEN configureSigning() THEN throw exception`() {
        // GIVEN
        val project: Project = mockk(relaxed = false)
        val extension: PublishPluginExtension = mockk(relaxed = true)
        val signingExtension: SigningExtension = mockk(relaxed = true)
        every { extension.signingEnabled.get() } returns true
        stubGradleAction(signingExtension) { project.extensions.configure(SigningExtension::class.java, it) }
        every { project.extensions.getByName("publishing") } returns mockk<PublishingExtension>(relaxed = true)
        every { project.findProperty(any()) } returnsMany listOf(
            null,
            null,
            "signingKey",
            null,
        )

        // WHEN//THEN
        Assertions.assertThrowsExactly(
            IllegalArgumentException::class.java,
        ) {
            testSubject.configureSigning(project, extension)
        }

        Assertions.assertThrowsExactly(
            IllegalArgumentException::class.java,
        ) {
            testSubject.configureSigning(project, extension)
        }
    }

    @Test
    fun `GIVEN project WHEN configureSigning() THEN signing configured`() {
        // GIVEN
        val project: Project = mockk(relaxed = false)
        val extension: PublishPluginExtension = mockk(relaxed = false)
        val signingExtension: SigningExtension = mockk(relaxed = true)
        val publishingExtension: PublishingExtension = mockk(relaxed = false)
        val publicationContainer: PublicationContainer = mockk(relaxed = true)
        every { extension.signingEnabled.get() } returns true
        every { project.extensions.getByName("publishing") } returns publishingExtension
        every { publishingExtension.publications } returns publicationContainer
        stubGradleAction(signingExtension) { project.extensions.configure(SigningExtension::class.java, it) }
        every { project.findProperty(any()) } returnsMany listOf(
            "signingKey",
            "signingPassword",
        )

        // WHEN
        testSubject.configureSigning(project, extension)

        // THEN
        verifyAll {
            extension.signingEnabled.get()

            project.extensions.getByName("publishing")
            publishingExtension.publications

            project.extensions.configure(SigningExtension::class.java, any())

            project.findProperty("signing.key")
            project.findProperty("signing.password")
            signingExtension.isRequired = true
            signingExtension.useInMemoryPgpKeys("signingKey", "signingPassword")

            signingExtension.sign(publicationContainer)
        }

        confirmVerified(project, extension, signingExtension, publishingExtension, publicationContainer)
    }

    @Test
    fun `GIVEN project WHEN apply() THEN all configured`() {
        // GIVEN
        val project: Project = mockk(relaxed = true)
        val spyTestSubject = spyk(testSubject)
        val extension: PublishPluginExtension = mockk(relaxed = true)
        every { project.extensions.create("publishConfig", PublishPluginExtension::class.java) } returns extension
        every { extension.signingEnabled.get() } returns false

        // WHEN
        spyTestSubject.apply(project)

        // THEN
        verifyOrder {
            spyTestSubject.apply(project)
            spyTestSubject.addPlugins(project)
            spyTestSubject.addExtension(project)
            spyTestSubject.configurePublishing(project, extension)
            spyTestSubject.configureSigning(project, extension)
        }

        confirmVerified(spyTestSubject)
    }
}
