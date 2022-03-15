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

package eu.bitfunk.gradle.version.catalog.intern

import eu.bitfunk.gradle.version.catalog.VersionCatalogHelperContract.Mapper
import eu.bitfunk.gradle.version.catalog.intern.model.Catalog
import eu.bitfunk.gradle.version.catalog.intern.model.Node
import eu.bitfunk.gradle.version.catalog.intern.test.FileHelper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GeneratorTest {

    lateinit var mapper: Mapper

    @BeforeEach
    fun setup() {
        mapper = mockk()
        every { mapper.map(emptyList()) } returns emptyList()
    }

    @Test
    fun `GIVEN empty catalog WHEN generate() THEN return empty helper`() {
        // GIVEN
        val baseName = "Empty"
        val catalog = Catalog(
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList()
        )
        val generator = Generator(PACKAGE_NAME, baseName, mapper)

        // WHEN
        val result = generator.generate(catalog)

        // THEN
        val expected = FileHelper.loadAsString("fixture/EmptyVersionCatalogHelper.kt")
        Assertions.assertEquals(expected, result)
    }

    companion object {
        const val PACKAGE_NAME = "com.example.catalog"
    }
}
