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

import io.mockk.every
import io.mockk.mockk
import org.gradle.api.provider.Property
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PublishPluginExtensionTest {

    private lateinit var propertyString: Property<String>
    private lateinit var propertyBoolean: Property<Boolean>

    private lateinit var testSubject: TestPublishPluginExtension

    @BeforeEach
    fun setup() {
        propertyString = mockk()
        propertyBoolean = mockk()

        testSubject = TestPublishPluginExtension(propertyString, propertyBoolean)
    }

    @Test
    fun `implements contract`() {
        assertInstanceOf(
            PublishContract.Extension::class.java, testSubject
        )
    }

    @Test
    fun `GIVEN extension configured WHEN isConfigured() THEN true`() {
        // GIVEN
        every { propertyString.isPresent } returns true
        every { propertyBoolean.isPresent } returns true

        // WHEN
        val result = testSubject.isConfigured()

        // THEN
        assertTrue(result)
    }

    @Test
    fun `GIVEN extension not configured WHEN isConfigured() THEN false`() {
        // GIVEN
        every { propertyString.isPresent } returns false
        every { propertyBoolean.isPresent } returns false

        // WHEN
        val result = testSubject.isConfigured()

        // THEN
        assertFalse(result)
    }

    internal class TestPublishPluginExtension(
        private val stringProperty: Property<String>,
        private val booleanProperty: Property<Boolean>
    ) : PublishPluginExtension() {

        override val projectName: Property<String>
            get() = stringProperty
        override val projectDescription: Property<String>
            get() = stringProperty
        override val projectUrl: Property<String>
            get() = stringProperty
        override val licenseName: Property<String>
            get() = stringProperty
        override val licenseUrl: Property<String>
            get() = stringProperty
        override val developerName: Property<String>
            get() = stringProperty
        override val developerEmail: Property<String>
            get() = stringProperty
        override val organizationName: Property<String>
            get() = stringProperty
        override val organizationUrl: Property<String>
            get() = stringProperty
        override val scmUrl: Property<String>
            get() = stringProperty
        override val issueManagement: Property<String>
            get() = stringProperty
        override val issueUrl: Property<String>
            get() = stringProperty
        override val signingEnabled: Property<Boolean>
            get() = booleanProperty
    }
}
