package com.pascalwelsch.konduit.widget

fun WidgetListBuilder.progressBar(init: ProgressBarWidget.() -> Unit) = add(ProgressBarWidget(), init)

open class ProgressBarWidget : Widget() {

    /**
     * float between 0f and 1f
     */
    var progress: Float = 0f
        set(value) {
            checkWritability()
            field = value
        }

    var isIndeterminate: Boolean = false
        set(value) {
            checkWritability()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProgressBarWidget) return false
        if (!super.equals(other)) return false

        if (progress != other.progress) return false
        if (isIndeterminate != other.isIndeterminate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + progress.hashCode()
        result = 31 * result + isIndeterminate.hashCode()
        return result
    }
}