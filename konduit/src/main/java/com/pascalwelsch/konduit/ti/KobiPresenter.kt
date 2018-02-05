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

package com.pascalwelsch.konduit.ti

import android.support.annotation.VisibleForTesting
import android.util.Log
import com.pascalwelsch.konduit.widget.Widget
import net.grandcentrix.thirtyinch.TiPresenter
import net.grandcentrix.thirtyinch.TiView
import java.lang.IllegalStateException
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

/**
 * Creates a list of [Widget] whenever the state changes and binds the latest data to the currently attached view [V]. With clever
 * diffing [BoundView.render] is only called when the result of [build] really changes
 */
abstract class KonduitPresenter<V : BoundView> : TiPresenter<V>() {

    @VisibleForTesting
    var renderThreadExecutor: Executor = Executors.newSingleThreadExecutor()

    private val TAG = KonduitPresenter::class.java.simpleName

    @Volatile
    private var dirty = false

    private val renderingLock = object {}

    @Volatile
    private var lastRenderedWidgets: List<Widget>? = null

    init {
        val executor = renderThreadExecutor
        if (executor is ThreadPoolExecutor) {
            if (executor.maximumPoolSize > 1) {
                throw IllegalStateException("Don't use an Execute with multiple threads, one is enough")
            }
        }
    }

    /**
     * Builds a Widget tree which represents the UI state. Will be called initially and every time the
     * state changes (triggered by calling [setState])
     */
    protected abstract fun build(context: BuildContext): List<Widget>

    protected fun setState(block: () -> Unit) {
        block()
        dispatchRender()
    }

    override fun onAttachView(view: V) {
        super.onAttachView(view)

        dispatchRender()
    }

    override fun onDetachView() {
        super.onDetachView()
        lastRenderedWidgets = null
        dirty = false
    }

    private fun dispatchRender() {
        if (view == null) {
            Log.v(TAG, "no view, don't render. Next attach will trigger render again")
            return
        }

        synchronized(renderingLock) {
            if (dirty) {
                // render already triggered but not yet rendered.
                return
            }
            dirty = true
        }

        renderThreadExecutor.execute {
            val view = this@KonduitPresenter.view
            if (view == null) {
                Log.v(TAG, "view lost, don't render. Wait for next attach")
                return@execute
            }

            while (dirty) {
                val widgets = synchronized(renderingLock) {
                    val widgets = build(view.getBuildContext())
                    dirty = false
                    widgets
                }

                // make all immutable
                widgets.forEach { it.lock() }

                if (widgets == lastRenderedWidgets) {
                    // widgets are the same, don't render
                    continue
                }

                view.render(widgets)
                lastRenderedWidgets = widgets
            }
        }
    }
}

//TODO find better name. KonduitView?
interface BoundView : TiView {

    fun getBuildContext(): BuildContext

    /**
     * new ui state as widget.
     *
     * Called on the render thread (off the main thread)
     */
    fun render(widgets: List<Widget>)
}

interface BuildContext {
    fun getString(id: Any, vararg formatArgs: Any): String
    fun viewById(key: Any): Int?
    fun getLocale(): List<Locale>
}
