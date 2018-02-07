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

package com.pascalwelsch.konduit.test

import com.nhaarman.mockito_kotlin.*
import com.pascalwelsch.konduit.BuildContext
import com.pascalwelsch.konduit.KonduitPresenter
import com.pascalwelsch.konduit.KonduitView
import com.pascalwelsch.konduit.widget.InputWidget
import com.pascalwelsch.konduit.widget.TextWidget
import com.pascalwelsch.konduit.widget.Widget
import com.pascalwelsch.konduit.widget.findByKey
import net.grandcentrix.thirtyinch.test.TiTestPresenter
import java.util.Locale
import java.util.concurrent.Executor

/**
 * Makes the [KonduitPresenter] testable. Returns an test UI which can be used to make assertions on the currently visible [Widget]s.
 *
 * ```
 * val ui = presenter.testUi()
 * ui.widget<Text>(R.id.result).hasText("")
 * ui.widget<Button>(R.id.button).click()
 * ui.widget<Text>(R.id.result).hasText(ui.i18n(R.string.success))
 * ```
 */
inline fun <reified V : KonduitView, P : KonduitPresenter<V>> P.testUi(
        context: BuildContext? = null): KobiTestableUi<P, V> {
    val testPresenter = this.test()
    renderThreadExecutor = Executor { it.run() }

    val buildContext: BuildContext = context ?: object :
            BuildContext {
        override fun getLocale(): List<Locale> = listOf(Locale("test"))
        override fun viewById(key: Any): Int = key as Int
        override fun getString(id: Any, vararg formatArgs: Any): String {
            return "mocked-stringRes-$id" + formatArgs.joinToString(separator = "-", prefix = "-")
        }
    }

    val renderCaptor = argumentCaptor<List<Widget>>()

    val view = mock<V> {
        // do nothing, just observe current render state
        on({ render(renderCaptor.capture()) }) doAnswer { /*noop*/ }
        on({ getBuildContext() }) doReturn buildContext
    }
    testPresenter.attachView(view)

    return KobiTestableUi(this, testPresenter, renderCaptor, buildContext)
}

class KobiTestableUi<out P : KonduitPresenter<V>, V : KonduitView>(
        val presenter: P,
        val instructor: TiTestPresenter<V>,
        val captor: KArgumentCaptor<List<Widget>>,
        val context: BuildContext
) {

    val view: V get() = presenter.view!!

    /**
     * finds the widget in the last rendered widget tree. Don't save the reference the reference changes after every call to render()
     */
    fun <T : Widget> widget(key: Any): T {
        val widget = captor.lastValue.findByKey(key)
        if (widget == null) throw IllegalStateException("widget with id $key not found")
        // explicitly don't use a reified inline function which creates unreadable stacktraces
        @Suppress("UNCHECKED_CAST")
        return widget as T
    }

    fun widgetIsAbsent(key: Any) {
        val widget = captor.lastValue.findByKey(key)
        if (widget != null) throw IllegalStateException("found widget which should be absent $widget")
    }

    fun i18n(stringId: Int): String {
        return context.getString(stringId)
    }
}

// like apply but fits better semantically
inline fun <T> T.verify(block: T.() -> Unit): T {
    block(); return this
}

// like apply but fits better for actions
inline fun <T> T.perform(block: T.() -> Unit): T {
    block(); return this
}

fun <T : Widget> T.isEnabled(): T {
    if (!enabled) throw IllegalStateException("widget not enabled $this")
    return this
}

fun <T : Widget> T.isVisible(): T {
    if (!visible) throw IllegalStateException("widget not visible $this")
    return this
}

fun <T : Widget> T.isInvisible(): T {
    if (visible) throw IllegalStateException("widget visible $this")
    return this
}

fun <T : Widget> T.isDisabled(): T {
    if (enabled) throw IllegalStateException("widget not disabled $this")
    return this
}

fun Widget.click(): Widget {
    kotlin.require(enabled) { "can't click on disabled view $this" }
    onClick!!.invoke()
    return this
}

fun InputWidget.typeText(text: String): InputWidget {
    kotlin.require(enabled) { "can't type text in disabled text field $this" }
    onTextChanged!!.invoke(text)
    return this
}

fun TextWidget.hasText(text: String): TextWidget {
    if (this.text != text) {
        throw IllegalArgumentException("text '${this.text}' doesn't match expected text '$text'")
    }
    return this
}

fun TextWidget.isEmpty(): TextWidget {
    if (text?.isEmpty() != true) {
        throw IllegalArgumentException("text '${this.text}' should be empty")
    }
    return this
}