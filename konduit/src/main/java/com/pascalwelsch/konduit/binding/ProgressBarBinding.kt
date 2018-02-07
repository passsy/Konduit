package com.pascalwelsch.konduit.binding

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.ProgressBar
import com.pascalwelsch.konduit.AndroidViewBinding
import com.pascalwelsch.konduit.widget.ProgressBarWidget
import com.pascalwelsch.konduit.widget.Widget

class ProgressBarBinding(private val progressBar: ProgressBar) : AndroidViewBinding {

    private var initialState: ProgressBarWidget? = null

    override fun onAdded(widget: Widget) {
        initialState = ProgressBarWidget().apply {
            progress = progressBar.progress.toFloat() / (progressBar.max - progressBar.minCompat())
        }
    }

    override fun onRemoved(widget: Widget) {
        initialState?.let { onChanged(it) }
    }

    override fun onChanged(widget: Widget) {
        if (widget !is ProgressBarWidget) return
        progressBar.progress = Math.round(widget.progress * progressBar.max + progressBar.minCompat())
    }

    private fun ProgressBar.minCompat() = if (VERSION.SDK_INT >= VERSION_CODES.O) min else 0
}