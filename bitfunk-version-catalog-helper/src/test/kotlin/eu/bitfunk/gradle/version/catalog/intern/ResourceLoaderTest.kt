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

import eu.bitfunk.gradle.version.catalog.VersionCatalogHelperContract
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ResourceLoaderTest {

    @Test
    fun `generator implements contract`() {
        Assertions.assertInstanceOf(
            VersionCatalogHelperContract.ResourceLoader::class.java,
            ResourceLoader
        )
    }

    @Test
    fun `GIVEN no resource WHEN loadAsString() THEN throw`() {
        // GIVEN
        val fileName = "NoFile.exists"

        // WHEN/THEN
        Assertions.assertThrowsExactly(
            NullPointerException::class.java,
            { ResourceLoader.loadAsString(fileName) },
            "$fileName does not exist"
        )
    }

    @Test
    fun `GIVEN resource WHEN loadAsString() THEN return file content`() {
        // GIVEN
        val fileName = "sources/Dependency.kt"

        // WHEN
        val result = ResourceLoader.loadAsString(fileName)

        // THEN
        Assertions.assertNotNull(result)
    }
}
