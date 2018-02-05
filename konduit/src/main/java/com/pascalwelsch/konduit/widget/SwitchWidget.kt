package com.pascalwelsch.konduit.widget

fun WidgetListBuilder.switch(init: SwitchWidget.() -> Unit) = add(SwitchWidget(), init)

class SwitchWidget : Widget() {
    /**
     * emits the current value when the user changes it.
     */
    var onSwitch: ((Boolean) -> Unit)? = null
        set(value) {
            checkWritability()
            field = value
        }

    var value: Boolean = false
        set(value) {
            checkWritability()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SwitchWidget) return false
        if (!super.equals(other)) return false

        if (onSwitch != other.onSwitch) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (onSwitch?.hashCode() ?: 0)
        result = 31 * result + value.hashCode()
        return result
    }
}