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