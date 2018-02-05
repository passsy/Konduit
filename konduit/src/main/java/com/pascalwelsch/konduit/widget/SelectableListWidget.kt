package com.pascalwelsch.konduit.widget

fun <T> WidgetListBuilder.singleSelectionListView(
        init: SingleSelectableListWidget<T>.() -> Unit): SingleSelectableListWidget<T> = add(
        SingleSelectableListWidget(), init)

class SingleSelectableListWidget<T> : ListWidget<T>() {

    var selectedItem: T? = null
        set(value) {
            checkWritability()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SingleSelectableListWidget<*>) return false
        if (!super.equals(other)) return false

        if (selectedItem != other.selectedItem) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (selectedItem?.hashCode() ?: 0)
        return result
    }
}