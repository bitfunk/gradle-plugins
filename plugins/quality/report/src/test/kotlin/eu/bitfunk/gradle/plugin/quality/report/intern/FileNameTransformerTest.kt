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

package eu.bitfunk.gradle.plugin.quality.report.intern

import org.gradle.api.Transformer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class FileNameTransformerTest {

    private lateinit var testSubject: FileNameTransformer

    @BeforeEach
    fun setup() {
        testSubject = FileNameTransformer()
    }

    @Test
    fun `implements Transformer`() {
        assertInstanceOf(
            Transformer::class.java,
            testSubject
        )
    }

    @Test
    fun `GIVEN one fileName WHEN transform() THEN -1 added to fileName`() {
        // GIVEN
        val fileName = "filename.xml"

        // WHEN
        val result = testSubject.transform(fileName)

        // THEN
        assertEquals(
            "filename-1.xml",
            result
        )
    }

    @Test
    fun `GIVEN one fileName WHEN transform() 3 times THEN -3 added to fileName`() {
        // GIVEN
        val fileName = "filename.xml"

        // WHEN
        var result: String = ""
        repeat(3) { result = testSubject.transform(fileName) }

        // THEN
        assertEquals(
            "filename-3.xml",
            result
        )
    }
}
