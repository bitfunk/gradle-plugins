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

package eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern

import eu.bitfunk.gradle.plugin.development.version.catalog.accessor.intern.test.FileHelper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VersionInterfaceGeneratorTest {

    private lateinit var testSubject: VersionInterfaceGenerator

    @BeforeEach
    fun setup() {
        testSubject = VersionInterfaceGenerator()
    }

    @Test
    fun `generator implements contract`() {
        assertInstanceOf(
            InternalContract.VersionInterfaceGenerator::class.java,
            testSubject
        )
    }

    @Test
    fun `GIVEN packageName WHEN generate() THEN`() {
        // GIVEN
        val packageName = "com.example.catalog"
        val expected = FileHelper.loadAsString("fixture/VersionCatalogDependency.kt")

        // WHEN
        val result = testSubject.generate(packageName)

        // THEN
        assertEquals(expected, result)
    }

}
