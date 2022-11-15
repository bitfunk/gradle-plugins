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

package eu.bitfunk.gradle.plugin.development.test.util

import io.mockk.MockKMatcherScope
import io.mockk.every
import io.mockk.slot
import org.gradle.api.Action

public fun <T : Any> stubGradleAction(
    answer: T,
    every: MockKMatcherScope.(Action<T>) -> Unit
) {
    val actionSlot = slot<Action<T>>()
    every {
        every(capture(actionSlot))
    } answers {
        actionSlot.captured.execute(answer)
    }
}

public fun <T : Any, R : Any> stubGradleActionWithReturn(
    answer: T,
    returnValue: R,
    every: MockKMatcherScope.(Action<T>) -> R
) {
    val actionSlot = slot<Action<T>>()
    every {
        every(capture(actionSlot))
    } answers {
        actionSlot.captured.execute(answer)
        returnValue
    }
}
