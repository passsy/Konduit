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

fun WidgetListBuilder.radioGroup(init: RadioGroupWidget.() -> Unit) = add(RadioGroupWidget(), init)

class RadioGroupWidget : Widget() {

    var onCheckedChange: ((Int) -> Unit)? = null
        set(value) {
            checkWritability()
            field = value
        }

    var checkedId: Int = 0
        set(value) {
            checkWritability()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RadioGroupWidget) return false
        if (!super.equals(other)) return false

        if (onCheckedChange != other.onCheckedChange) return false
        if (checkedId != other.checkedId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (onCheckedChange?.hashCode() ?: 0)
        result = 31 * result + checkedId.hashCode()
        return result
    }
}