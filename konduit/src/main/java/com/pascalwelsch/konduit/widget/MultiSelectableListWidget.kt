package de.insta.upb.kobi

import com.pascalwelsch.konduit.widget.ListWidget
import com.pascalwelsch.konduit.widget.WidgetListBuilder

fun <T> WidgetListBuilder.multiSelectableListView(
        init: MultiSelectableListWidget<T>.() -> Unit): MultiSelectableListWidget<T> = add(
        MultiSelectableListWidget(), init)

/**
 * Widget for lists that have a selection that can be of a different type
 * @param T The list item type
 */
open class MultiSelectableListWidget<T> : ListWidget<T>() {

    var selectedItems: List<T> = listOf()
        set(value) {
            checkWritability()
            // copy list items. This doesn't prevent mutable list items but at least makes the list immutable
            field = value.toList()
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MultiSelectableListWidget<*>) return false
        if (!super.equals(other)) return false

        if (selectedItems != other.selectedItems) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + selectedItems.hashCode()
        return result
    }
}
