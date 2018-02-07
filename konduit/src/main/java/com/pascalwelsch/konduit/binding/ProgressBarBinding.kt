package com.pascalwelsch.konduit.binding

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import android.widget.ProgressBar
import com.pascalwelsch.konduit.ViewBinding
import com.pascalwelsch.konduit.ViewBindingAdapters
import com.pascalwelsch.konduit.widget.ProgressBarWidget

class ProgressBarBindingAdapters : ViewBindingAdapters {
    override fun createBinding(view: View, emit: (ViewBinding<*>) -> Unit) {
        if (view is ProgressBar) {
            emit(ProgressBarBinding(view))
        }
    }
}

private class ProgressBarBinding(private val progressBar: ProgressBar) :
        ViewBinding<ProgressBarWidget> {

    private var initialState: ProgressBarWidget? = null

    override fun onAdded(widget: ProgressBarWidget) {
        initialState = ProgressBarWidget().apply {
            progress = progressBar.progress.toFloat() / (progressBar.max - progressBar.minCompat())
        }
    }

    override fun onRemoved(widget: ProgressBarWidget) {
        initialState?.let { onChanged(it) }
    }

    override fun onChanged(widget: ProgressBarWidget) {
        progressBar.progress = Math.round(widget.progress * progressBar.max + progressBar.minCompat())
    }

    private fun ProgressBar.minCompat() = if (VERSION.SDK_INT >= VERSION_CODES.O) min else 0
}