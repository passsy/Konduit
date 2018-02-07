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

fun WidgetListBuilder.seekBar(init: SeekBarWidget.() -> Unit) = add(SeekBarWidget(), init)

class SeekBarWidget : ProgressBarWidget() {
    /**
     * emits the current value when the user changes it. Range 0..100
     */
    var onSeek: ((Int) -> Unit)? = null
        set(value) {
            checkWritability()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SeekBarWidget) return false
        if (!super.equals(other)) return false

        if (onSeek != other.onSeek) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (onSeek?.hashCode() ?: 0)
        return result
    }
}