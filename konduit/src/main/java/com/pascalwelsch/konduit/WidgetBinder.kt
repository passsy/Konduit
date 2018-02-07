/*
 * Copyright (C) 2018 Pascal Welsch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pascalwelsch.konduit

import android.util.Log
import com.pascalwelsch.konduit.widget.Widget

/**
 * Friendly to work with [AndroidViewRenderer]
 */
interface WidgetBinder {

    /**
     * use this renderer directly for more advanced features, like removing [ViewBindingAdapter]s
     */
    val renderer: AndroidViewRenderer

    /**
     * Create a binding for a single key which can have a dynamic View.
     */
    fun <W : Widget> bind(key: Any, onChange: ((W) -> Unit)? = null, onAdded: ((W) -> Unit)? = null,
            onRemoved: ((W) -> Unit)? = null) {
        bind(key, object : ViewBinding<W> {
            override fun onChanged(widget: W) {
                onChange?.invoke(widget)
            }

            override fun onAdded(widget: W) {
                onAdded?.invoke(widget)
            }

            override fun onRemoved(widget: W) {
                onRemoved?.invoke(widget)
            }
        })
    }

    /**
     * Create a binding for a single key which can have a dynamic View.
     */
    fun <W : Widget> bind(key: Any, binding: ViewBinding<W>) {
        val bindings = renderer.bindingsFor(key)

        if (bindings.count() > 0) {
            Log.v("Bind", " - $key already has ${bindings.count()} bindings")
            Log.v("Bind", " - bindings: ${bindings.joinToString("\n")}")
        }

        bindings.add(binding)
    }

    /**
     * Adds a [ViewBindingAdapter] which generates bindings for all matching [Widget]s
     */
    fun addAdapter(adapter: ViewBindingAdapter) {
        renderer.adapters.add(adapter)
    }
}