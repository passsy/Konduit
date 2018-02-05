package com.pascalwelsch.konduit.widget

fun WidgetListBuilder.toolbarWidget(init: ToolbarWidget.() -> Unit) = add(ToolbarWidget(), init)

class ToolbarWidget : Widget() {
    var title: String? = null
        set(value) {
            checkWritability()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ToolbarWidget) return false
        if (!super.equals(other)) return false

        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        return result
    }
}