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

fun WidgetListBuilder.spinnerWidget(init: SpinnerWidget.() -> Unit) = add(SpinnerWidget(), init)

class SpinnerWidget : Widget() {

    var selectedItem: Int = 0
        set(value) {
            checkWritability()
            field = value
        }

    var layoutResourceId: Int = 0
        set(value) {
            checkWritability()
            field = value
        }

    var dropDownLayoutResourceId: Int = 0
        set(value) {
            checkWritability()
            field = value
        }

    var items: Array<String> = emptyArray()
        set(value) {
            checkWritability()
            field = value
        }

    var onItemSelected: ((Int) -> Unit)? = null
        set(value) {
            checkWritability()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SpinnerWidget) return false
        if (!super.equals(other)) return false

        if (selectedItem != other.selectedItem) return false
        if (layoutResourceId != other.layoutResourceId) return false
        if (dropDownLayoutResourceId != other.dropDownLayoutResourceId) return false
        if (!items.contentEquals(other.items)) return false
        if (onItemSelected != other.onItemSelected) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (selectedItem.hashCode())
        result = 31 * result + (layoutResourceId.hashCode())
        result = 31 * result + (dropDownLayoutResourceId.hashCode())
        result = 31 * result + (items.contentHashCode())
        result = 31 * result + (onItemSelected?.hashCode() ?: 0)
        return result
    }
}