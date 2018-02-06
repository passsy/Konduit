/*
 * Copyright (C) 2017 Pascal Welsch
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

import android.annotation.TargetApi
import android.app.Activity
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import com.pascalwelsch.konduit.binding.AndroidViewBinding
import com.pascalwelsch.konduit.binding.ProgressBarBinding
import com.pascalwelsch.konduit.binding.SeekBarBinding
import com.pascalwelsch.konduit.binding.SwitchBinding
import com.pascalwelsch.konduit.binding.TextViewBinding
import com.pascalwelsch.konduit.binding.ViewBinding
import com.pascalwelsch.konduit.widget.Widget
import com.pascalwelsch.konduit.widget.findByKey
import java.util.Collections.emptyList
import java.util.Locale

private val TAG = AndroidViewRenderer::class.java.simpleName
private val DEBUG = true

open class AndroidViewRenderer(private val activity: Activity) : KonduitView {

    fun autobindAllViews(view: View) {
        view.flatChildren.filter { it.id > 0 }.forEach { v ->
            autobind(v)
        }
    }

    val autoBindings = mutableListOf<(View, add: (AndroidViewBinding) -> Unit) -> Unit>(
            { view, add -> add(ViewBinding(view)) },
            { view, add -> if (view is TextView) add(TextViewBinding(view)) },
            { view, add -> if (view is Switch) add(SwitchBinding(view)) },
            { view, add -> if (view is ProgressBar) add(ProgressBarBinding(view)) },
            { view, add -> if (view is SeekBar) add(SeekBarBinding(view)) }
    )

    fun autobind(view: View, key: Any = view.id) {
        val bindings = bindingsFor(key)

        Log.v("Bind", "autobinding $view")
        if (bindings.count() > 0) {
            Log.v("Bind", " - ${view.javaClass.simpleName} already has ${bindings.count()} bindings")
            Log.v("Bind", " - bindings: ${bindings.joinToString("\n")}")
        }

        // add matching auto bindings
        autoBindings.forEach { binding -> binding(view, { bindings.add(it) }) }
    }

    inline fun <reified W : Widget> bind(view: View,
            crossinline onChange: (W) -> Unit,
            noinline onAdded: ((W) -> Unit)? = null,
            noinline onRemoved: ((W) -> Unit)? = null){
        Log.v("Bind", "onChanged ${view.javaClass.simpleName} $view")
        bind(view.id, onChange, onAdded, onRemoved)
    }

    inline fun <reified W : Widget> bind(
            key: Any,
            crossinline onChange: (W) -> Unit,
            noinline onAdded: ((W) -> Unit)? = null,
            noinline onRemoved: ((W) -> Unit)? = null) {
        val bindings = bindingsFor(key)

        if (bindings.count() > 0) {
            Log.v("Bind", " - $key already has ${bindings.count()} bindings")
            Log.v("Bind", " - bindings: ${bindings.joinToString("\n")}")
        }

        bindings.add(object : AndroidViewBinding {
            override fun onChanged(widget: Widget) {
                if (widget is W) {
                    onChange(widget)
                } else {
                    throw IllegalStateException(
                            "Internal error, triggered binding for the wrong binding adapter. key: $key, widget: $widget")
                }
            }

            override fun onAdded(widget: Widget) {
                if (widget is W) {
                    onAdded?.invoke(widget)
                } else {
                    throw IllegalStateException(
                            "Internal error, triggered binding for the wrong binding adapter. key: $key, widget: $widget")
                }
            }

            override fun onRemoved(widget: Widget) {
                if (widget is W) {
                    onRemoved?.invoke(widget)
                } else {
                    throw IllegalStateException(
                            "Internal error, triggered binding for the wrong binding adapter. key: $key, widget: $widget")
                }
            }
        })
    }

    private var lastRenderedWidgets: List<Widget> = emptyList()

    val viewBindings: HashMap<Any, MutableList<AndroidViewBinding>> = hashMapOf()

    fun bindingsFor(key: Any): MutableList<AndroidViewBinding> {
        return viewBindings[key]
                ?: mutableListOf<AndroidViewBinding>().apply { viewBindings[key] = this }
    }

    @Synchronized
    override fun render(widgets: List<Widget>) {

        if (widgets == lastRenderedWidgets) {
            // render not required, virtual view hasn't changed
            if (DEBUG) Log.v(TAG, "render(): skip render - widget did not change")
            return
        }

        widgets.forEach {
            require(it.key != null) { "widget $it has no key" }
        }

        val newUi = widgets
        val oldUi = lastRenderedWidgets

        //removals
        val removed = oldUi
                .filter { it.key != null }
                .filter { newUi.findByKey(it.key) == null }

        //additions
        val added = newUi.filter { it.key != null }
                .filter { oldUi.findByKey(it.key) == null }

        //changes
        val changed = newUi.filter { it.key != null }
                .filter { !added.contains(it) }
                .filter { oldUi.findByKey(it.key) != it }

        // save for next render
        lastRenderedWidgets = widgets

        activity.runOnUiThread {
            removed.forEach {
                onWidgetRemoved(it)
            }

            added.forEach {
                onWidgetAdded(it)
            }

            changed.forEach {
                onWidgetChanged(it)
            }
        }
    }

    override fun getBuildContext(): BuildContext {
        return object : BuildContext {
            override fun viewById(key: Any): Int? {
                if (key is Int) {
                    // assume it's a R.id.*
                    return key
                }
                if (key is String) {
                    // assume it's the name of R.id.<name>
                    val resId = activity.resources.getIdentifier(key, "id", activity.packageName)
                    if (resId > 0) {
                        return resId
                    }
                }

                // please provide your own mapping
                throw IllegalStateException("no view found for key $key")
            }

            override fun getString(id: Any, vararg formatArgs: Any): String {
                if (id is Int) {
                    return if (formatArgs.isNotEmpty()) {
                        activity.getString(id, *formatArgs)
                    } else {
                        activity.getString(id)
                    }
                }

                // please provide your own mapping
                throw IllegalStateException("no string found for id $id")
            }

            override fun getLocale(): List<Locale> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return activity.resources.configuration.locales.toCollection().toList()
                } else {
                    @Suppress("DEPRECATION")
                    return listOf(activity.resources.configuration.locale)
                }
            }
        }
    }

    protected open fun onWidgetRemoved(widget: Widget) {
        if (DEBUG) Log.v(TAG, "removed $widget")
        widget.bindingsForEach { it.onRemoved(widget) }
    }

    protected open fun onWidgetAdded(widget: Widget) {
        if (DEBUG) Log.v(TAG, "added $widget")
        widget.bindingsForEach {
            it.onAdded(widget)
            it.onChanged(widget)
        }
    }

    protected open fun onWidgetChanged(widget: Widget) {
        if (DEBUG) Log.v(TAG, "changed $widget")
        widget.bindingsForEach { it.onChanged(widget) }
    }

    private fun Widget.bindingsForEach(block: (AndroidViewBinding) -> Unit) {
        val bindings = viewBindings[key]
        if (bindings == null || bindings.isEmpty()) {
            throw IllegalStateException("widget ${this} cannot be bound. " +
                    "No binding exists for key ${resIdName(key!!)}")
        }
        bindings.forEach(block)
    }

    private fun resIdName(key: Any?): String? {
        if (key == null) return null

        // prettify android resource ids
        if (key is Int) {
            if (key < 0) return null
            val r = activity.resources ?: return null

            val hexId = Integer.toHexString(key)

            try {
                val pkgname = when (key and 0xff000000.toInt()) {
                    0x7f000000 -> "app"
                    0x01000000 -> "android"
                    else -> r.getResourcePackageName(key)
                }
                val entryname = r.getResourceEntryName(key)
                val typename = r.getResourceTypeName(key)

                return "$pkgname:$typename/$entryname (#$hexId)"
            } catch (e: Resources.NotFoundException) {
                // not found
                return "$key (#$hexId)"
            }
        }

        return key.toString()
    }
}

private inline val View.flatChildren: List<View>
    get() = if (this !is ViewGroup) {
        emptyList<View>()
    } else {
        (0..childCount - 1)
                .map { getChildAt(it) }
                .flatMap { listOf(it) + it.flatChildren }
    }

@TargetApi(Build.VERSION_CODES.N)
private fun LocaleList.toCollection(): Collection<Locale> {
    val list = this
    return object : Collection<Locale> {
        override val size: Int = list.size()

        override fun contains(element: Locale): Boolean = list.indexOf(element) != -1

        override fun containsAll(elements: Collection<Locale>): Boolean {
            elements.forEach {
                if (list.indexOf(it) == -1) return false
            }
            return true
        }

        override fun isEmpty(): Boolean = list.size() == 0

        override fun iterator(): Iterator<Locale> {
            return object : Iterator<Locale> {

                var i = -1
                override fun hasNext(): Boolean = list.size() < (i + 1)

                override fun next(): Locale {
                    if (!hasNext()) throw NoSuchElementException()
                    i++
                    return list.get(i)
                }
            }
        }
    }
}