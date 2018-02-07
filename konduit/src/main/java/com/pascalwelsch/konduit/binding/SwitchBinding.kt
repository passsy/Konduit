package com.pascalwelsch.konduit.binding

import android.widget.Switch
import com.pascalwelsch.konduit.AndroidViewBinding
import com.pascalwelsch.konduit.widget.SwitchWidget
import com.pascalwelsch.konduit.widget.Widget

class SwitchBinding(private val switch: Switch) : AndroidViewBinding {

    private var initialState: SwitchWidget? = null

    override fun onAdded(widget: Widget) {
        initialState = SwitchWidget().apply {
            checked = switch.isChecked
        }
    }

    override fun onRemoved(widget: Widget) {
        initialState?.let { onChanged(it) }
    }

    override fun onChanged(widget: Widget) {
        if (widget !is SwitchWidget) return

        switch.setOnCheckedChangeListener(null)
        switch.isChecked = widget.checked
        switch.setOnCheckedChangeListener { _, checked -> widget.onSwitch?.invoke(checked) }
    }
}