package com.pascalwelsch.konduit.widget

fun WidgetListBuilder.toggle(init: ToggleWidget.() -> Unit) = add(
        ToggleWidget(), init)

class ToggleWidget : Widget() {

    var value: Boolean = false
        set(value) {
            checkWritability()
            field = value
        }

    var text: String = ""
        set(value) {
            checkWritability()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ToggleWidget) return false
        if (!super.equals(other)) return false

        if (value != other.value) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + text.hashCode()
        return result
    }

}