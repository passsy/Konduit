package com.pascalwelsch.konduit.binding

import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.pascalwelsch.konduit.ViewBinding
import com.pascalwelsch.konduit.ViewBindingAdapters
import com.pascalwelsch.konduit.widget.SeekBarWidget

class SeekBarBindingAdapters : ViewBindingAdapters {
    override fun createBinding(view: View, emit: (ViewBinding<*>) -> Unit) {
        if (view is SeekBar) {
            emit(SeekBarBinding(view))
        }
    }
}

class SeekBarBinding(private val seekbar: SeekBar) : ViewBinding<SeekBarWidget> {

    override fun onAdded(widget: SeekBarWidget) {
    }

    override fun onRemoved(widget: SeekBarWidget) {
        // can't restore the onChanged listener
        seekbar.setOnSeekBarChangeListener(null)
    }

    override fun onChanged(widget: SeekBarWidget) {
        seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    widget.onSeek?.invoke(seekbar.progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}