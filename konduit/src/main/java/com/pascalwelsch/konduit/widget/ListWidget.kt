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

package com.pascalwelsch.konduit.widget

fun <T> WidgetListBuilder.singleSelectionListView(init: ListWidget<T>.() -> Unit): ListWidget<T> = add(ListWidget(),
        init)

open class ListWidget<T> : Widget() {

    open var items: List<T> = listOf()
        set(value) {
            checkWritability()
            // copy list items. This doesn't prevent mutable list items but at least makes the list immutable
            field = value.toList()
        }

    open var onItemClick: ((T) -> Unit)? = null
        set(value) {
            checkWritability()
            field = value
        }

    //region equals/hashcode/toString
    override fun nonDefaultStatesToString(): List<String> {
        return super.nonDefaultStatesToString() + "itemCount=${items.count()}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListWidget<*>) return false
        if (!super.equals(other)) return false

        if (items != other.items) return false
        if (onItemClick != other.onItemClick) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + items.hashCode()
        result = 31 * result + (onItemClick?.hashCode() ?: 0)
        return result
    }
    //endregion
}
