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