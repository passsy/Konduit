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

import android.annotation.TargetApi
import android.app.Activity
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.pascalwelsch.konduit.binding.ProgressBarBindingAdapter
import com.pascalwelsch.konduit.binding.SeekBarBindingAdapter
import com.pascalwelsch.konduit.binding.SwitchBindingAdapter
import com.pascalwelsch.konduit.binding.TextViewBindingAdapter
import com.pascalwelsch.konduit.binding.ViewBindingAdapter
import com.pascalwelsch.konduit.widget.Widget
import com.pascalwelsch.konduit.widget.findByKey
import java.util.Collections.emptyList
import java.util.Locale

private val TAG = AndroidViewRenderer::class.java.simpleName
private const val DEBUG = false

open class AndroidViewRenderer : KonduitView {

    var activity: Activity? = null

    private var lastRenderedWidgets: List<Widget> = emptyList()

    private val viewBindings: HashMap<Any, MutableList<ViewBinding<*>>> = hashMapOf()

    /**
     * all registered adapters. feel free to add and remove adapters
     */
    val adapters = mutableListOf(
            ViewBindingAdapter(),
            TextViewBindingAdapter(),
            SwitchBindingAdapter(),
            ProgressBarBindingAdapter(),
            SeekBarBindingAdapter())

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

        activity?.runOnUiThread {
            removed.forEach { widget ->
                if (DEBUG) Log.v(TAG, "removed $widget")
                widget.bindingsForEach { it.onRemoved(widget) }
            }

            added.forEach { widget ->
                if (DEBUG) Log.v(TAG, "added $widget")
                widget.bindingsForEach {
                    it.onAdded(widget)
                    it.onChanged(widget)
                }
            }

            changed.forEach { widget ->
                if (DEBUG) Log.v(TAG, "changed $widget")
                widget.bindingsForEach { it.onChanged(widget) }
            }
        }
    }

    override fun getBuildContext(): BuildContext {
        val activity = requireNotNull(activity)
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

    fun bindingsFor(key: Any): MutableList<ViewBinding<*>> {
        val id = if (key is View) key.id else key

        val bindings = viewBindings[id]
        if (bindings != null) return bindings

        val newBindings = mutableListOf<ViewBinding<*>>()
        viewBindings[id] = newBindings
        return newBindings
    }

    fun autobindAllViews(view: View) {
        view.flatChildren.filter { it.id > 0 }.forEach { v ->
            autobind(v)
        }
    }

    fun autobind(view: View) {
        val bindings = bindingsFor(view)

        Log.v("Bind", "autobinding $view")
        if (bindings.count() > 0) {
            Log.v("Bind", " - ${view.javaClass.simpleName} already has ${bindings.count()} bindings")
            Log.v("Bind", " - bindings: ${bindings.joinToString("\n")}")
        }

        // add matching auto bindings
        this.adapters.forEach { adapter -> adapter.createBinding(view, { bindings.add(it) }) }
    }

    private fun <T : Widget> T.bindingsForEach(block: (ViewBinding<T>) -> Unit) {
        val bindings = viewBindings[key]
        if (bindings == null || bindings.isEmpty()) {
            throw IllegalStateException("widget ${this} cannot be bound. " +
                    "No binding exists for key ${resIdName(key!!)}")
        }
        bindings.forEach {
            @Suppress("UNCHECKED_CAST")
            block(it as ViewBinding<T>)
        }
    }

    private fun resIdName(key: Any?): String? {
        if (key == null) return null

        // prettify android resource ids
        if (key is Int) {
            if (key < 0) return null
            val r = activity?.resources ?: return null

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

/**
 * Factory to create a [ViewBinding]
 */
interface ViewBindingAdapter {

    /**
     * call [bindWith] with a new [ViewBinding] for [Widget] which can be handled, do nothing for non matching ones.
     */
    fun createBinding(view: View, bindWith: (ViewBinding<*>) -> Unit)
}

/**
 * Will be called to onChanged a widget to an arbitrary android view. This is the only connection between the two worlds
 */
interface ViewBinding<in W : Widget> {

    /**
     * Called when the [Widget] first appears. This is where dynamic views have to be initialized and added to
     * the window. Good bindings also save the current view state and restore it once the [Widget] will be removed.
     *
     * [onChanged] will be called directly afterwards, no need to bind everything here
     */
    fun onAdded(widget: W)

    /**
     * Called when the [Widget] changes, apply the new values to the bound View
     */
    fun onChanged(widget: W)

    /**
     * The [Widget] was removed, also remove the View or restore the previously saved values
     */
    fun onRemoved(widget: W)
}

val noopBinding = object : ViewBinding<Widget> {
    override fun onAdded(widget: Widget) {
        // noop
    }

    override fun onChanged(widget: Widget) {
        // noop
    }

    override fun onRemoved(widget: Widget) {
        // noop
    }
}

private inline val View.flatChildren: List<View>
    get() = if (this !is ViewGroup) {
        emptyList<View>()
    } else {
        (0 until childCount)
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