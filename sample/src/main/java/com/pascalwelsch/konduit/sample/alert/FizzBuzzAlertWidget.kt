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

package com.pascalwelsch.konduit.sample.alert

import android.support.v7.app.AlertDialog
import com.pascalwelsch.konduit.widget.Widget
import com.pascalwelsch.konduit.widget.WidgetListBuilder

/**
 * DSL access for the [FizzBuzzAlertWidget]
 */
fun WidgetListBuilder.friendAlert(init: FizzBuzzAlertWidget.() -> Unit): FizzBuzzAlertWidget = add(
        FizzBuzzAlertWidget(), init)

/**
 * Custom widget which represents a simple dialog
 *
 * Note that it doesn't implement all properties of [AlertDialog], only those which are required
 */
open class FizzBuzzAlertWidget : Widget() {

    open var message: String? = null
        set(value) {
            checkWritability()
            field = value
        }

    open var onDismiss: (() -> Unit)? = null
        set(value) {
            checkWritability()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FizzBuzzAlertWidget) return false
        if (!super.equals(other)) return false

        if (message != other.message) return false
        if (onDismiss != other.onDismiss) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + (onDismiss?.hashCode() ?: 0)
        return result
    }
}