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

package com.pascalwelsch.konduit.widget

@DslMarker
annotation class WidgetMarker

fun widgetList(init: WidgetListBuilder.() -> Unit): List<Widget> = WidgetListBuilder().apply(init)

fun WidgetListBuilder.widget(init: Widget.() -> Unit): Widget = add(Widget(), init)

@WidgetMarker
open class Widget {

    open var key: Any? = null
        set(value) {
            checkWritability()
            field = value
        }

    open var enabled: Boolean = true
        set(value) {
            checkWritability()
            field = value
        }
    open var visible: Boolean = true
        set(value) {
            checkWritability()
            field = value
        }
    open var onClick: (() -> Unit)? = null
        set(value) {
            checkWritability()
            field = value
        }

    @Volatile
    private var isWritable = true

    /**
     * Makes the widget immutable
     */
    fun lock() {
        isWritable = false
    }

    protected fun checkWritability() {
        if (!isWritable) {
            throw IllegalStateException("Once the model will be rendered, changing the data isn't allowed anymore")
        }
    }

    open fun nonDefaultStatesToString(): List<String> {
        return mutableListOf<String>().run {
            if (key != null) {
                if (key is Int) {
                    add("key=0x${Integer.toHexString(key as Int)}")
                } else {
                    add("key=$key")
                }
            }
            if (!enabled) add("enabled=false")
            if (!visible) add("visible=false")
            toList()
        }
    }

    override fun toString(): String {
        val props = nonDefaultStatesToString().joinToString()
        return "${javaClass.simpleName}($props)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Widget) return false

        if (key != other.key) return false
        if (enabled != other.enabled) return false
        if (visible != other.visible) return false
        if (onClick != other.onClick) return false
        if (isWritable != other.isWritable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key?.hashCode() ?: 0
        result = 31 * result + enabled.hashCode()
        result = 31 * result + visible.hashCode()
        result = 31 * result + (onClick?.hashCode() ?: 0)
        result = 31 * result + isWritable.hashCode()
        return result
    }
}

class WidgetListBuilder : MutableList<Widget> by mutableListOf() {

    inline fun <T : Widget> add(widget: T, init: T.() -> Unit): T {
        widget.apply(init)
        add(widget)
        return widget
    }
}

fun List<Widget>.findByKey(key: Any?): Widget? {
    if (key == null) return null
    return find { it.key == key }
}
