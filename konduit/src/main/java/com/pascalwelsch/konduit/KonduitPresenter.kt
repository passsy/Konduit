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

import android.support.annotation.VisibleForTesting
import com.pascalwelsch.konduit.widget.Widget
import net.grandcentrix.thirtyinch.TiLog
import net.grandcentrix.thirtyinch.TiPresenter
import net.grandcentrix.thirtyinch.TiView
import java.lang.IllegalStateException
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

/**
 * Creates a list of [Widget] whenever the state changes and binds the latest data to the currently attached view [V]. With clever
 * diffing [KonduitView.render] is only called when the result of [build] really changes
 */
abstract class KonduitPresenter<V : KonduitView> : TiPresenter<V>() {

    @VisibleForTesting
    var renderThreadExecutor: Executor = Executors.newSingleThreadExecutor()

    @Suppress("PrivatePropertyName")
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

    protected inline fun setState(block: () -> Unit) {
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

    fun dispatchRender() {
        if (view == null) {
            TiLog.v(TAG, "no view, don't render. Next attach will trigger render again")
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
                TiLog.v(TAG, "view lost, don't render. Wait for next attach")
                return@execute
            }

            while (dirty) {
                val widgets = synchronized(renderingLock) {
                    val widgets = build(view.getBuildContext())
                    dirty = false
                    widgets
                }

                exchangeListeners(widgets)

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

    /**
     * replaces listeners in widgets with static listeners widgets for the widget in the view to prevent the widgets
     * from changing all the time since lambdas instances are always different
     */
    private fun exchangeListeners(widgets: List<Widget>) {
        widgets.forEach { widget ->
            val clickListener = widget.onClick
            val callbackReferenceId = "${this::class.java.name}${widget::class.java.name}::${widget.key}"
            if (clickListener != null) {
                widgetCallbacks[callbackReferenceId] = clickListener
                var presenterCallback = presenterCallbacks[callbackReferenceId]
                if (presenterCallback == null) {
                    presenterCallback = object : (() -> Unit) {
                        override fun invoke() {
                            widgetCallbacks[callbackReferenceId]?.invoke()
                        }
                    }
                    presenterCallbacks[callbackReferenceId] = presenterCallback
                }
                widget.onClick = presenterCallback
            } else {
                widgetCallbacks.remove(callbackReferenceId)
                presenterCallbacks.remove(callbackReferenceId)
            }
        }
    }

    private var presenterCallbacks = mutableMapOf<String, () -> Unit>()
    private var widgetCallbacks = mutableMapOf<String, () -> Unit>()
}

/**
 * Contract between [KonduitPresenter] and [KonduitActivity]
 */
interface KonduitView : TiView {

    /**
     * access to UI specific information like strings
     */
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
    fun getLocale(): List<Locale>

    /**
     * maps keys from [KonduitPresenter] to keys used by the View
     */
    fun viewById(key: Any): Int?
}
