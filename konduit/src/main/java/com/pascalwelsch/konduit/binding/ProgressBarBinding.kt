package com.pascalwelsch.konduit.binding

import android.widget.ProgressBar
import com.pascalwelsch.konduit.widget.ProgressBarWidget
import com.pascalwelsch.konduit.widget.Widget

class ProgressBarBinding(private val progressBar: ProgressBar) : AndroidViewBinding {
    override fun onChanged(widget: Widget) {
        if (widget !is ProgressBarWidget) return

        progressBar.progress = Math.round(widget.progress * progressBar.max)
    }
}