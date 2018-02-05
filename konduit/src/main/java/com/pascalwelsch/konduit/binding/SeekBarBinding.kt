package com.pascalwelsch.konduit.binding

import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.pascalwelsch.konduit.widget.SeekBarWidget
import com.pascalwelsch.konduit.widget.Widget

class SeekBarBinding(private val seekbar: SeekBar) : AndroidViewBinding {
    override fun bind(widget: Widget) {
        if (widget !is SeekBarWidget) return
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