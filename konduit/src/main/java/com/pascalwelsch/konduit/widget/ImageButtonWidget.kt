package com.pascalwelsch.konduit.widget

fun WidgetListBuilder.imageButton(init: ImageButtonWidget.() -> Unit) = add(ImageButtonWidget(), init)

class ImageButtonWidget : ButtonWidget() {

    var drawableResourceId = 0
        set(value) {
            checkWritability()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImageButtonWidget) return false
        if (!super.equals(other)) return false

        if (drawableResourceId != other.drawableResourceId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + drawableResourceId
        return result
    }


}