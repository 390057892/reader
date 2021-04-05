/*
 * Copyright 2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.novel.read.lib.dialogs

import android.content.Context
import android.content.DialogInterface
import androidx.fragment.app.Fragment

typealias AlertBuilderFactory<D> = (Context) -> AlertBuilder<D>

inline fun <D : DialogInterface> Fragment.alert(
    noinline factory: AlertBuilderFactory<D>,
    title: String? = null,
    message: String? = null,
    noinline init: (AlertBuilder<D>.() -> Unit)? = null
) = activity?.alert(factory, title, message, init)

fun <D : DialogInterface> Context.alert(
    factory: AlertBuilderFactory<D>,
    title: String? = null,
    message: String? = null,
    init: (AlertBuilder<D>.() -> Unit)? = null
): AlertBuilder<D> {
    return factory(this).apply {
        if (title != null) {
            this.title = title
        }
        if (message != null) {
            this.message = message
        }
        if (init != null) init()
    }
}

inline fun <D : DialogInterface> Fragment.alert(
    noinline factory: AlertBuilderFactory<D>,
    titleResource: Int? = null,
    messageResource: Int? = null,
    noinline init: (AlertBuilder<D>.() -> Unit)? = null
) = requireActivity().alert(factory, titleResource, messageResource, init)

fun <D : DialogInterface> Context.alert(
    factory: AlertBuilderFactory<D>,
    titleResource: Int? = null,
    messageResource: Int? = null,
    init: (AlertBuilder<D>.() -> Unit)? = null
): AlertBuilder<D> {
    return factory(this).apply {
        if (titleResource != null) {
            this.titleResource = titleResource
        }
        if (messageResource != null) {
            this.messageResource = messageResource
        }
        if (init != null) init()
    }
}

inline fun <D : DialogInterface> Fragment.alert(
    noinline factory: AlertBuilderFactory<D>,
    noinline init: AlertBuilder<D>.() -> Unit
) = requireActivity().alert(factory, init)

fun <D : DialogInterface> Context.alert(
    factory: AlertBuilderFactory<D>,
    init: AlertBuilder<D>.() -> Unit
): AlertBuilder<D> = factory(this).apply { init() }
