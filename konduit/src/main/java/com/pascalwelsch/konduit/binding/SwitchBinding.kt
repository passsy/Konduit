package com.pascalwelsch.konduit.binding

import android.widget.Switch
import com.pascalwelsch.konduit.widget.SwitchWidget
import com.pascalwelsch.konduit.widget.Widget

class SwitchBinding(private val switch: Switch) : AndroidViewBinding {
    override fun bind(widget: Widget) {
        if (widget !is SwitchWidget) return

        switch.setOnCheckedChangeListener(null)
        switch.isChecked = widget.value
        switch.setOnCheckedChangeListener { _, checked -> widget.onSwitch?.invoke(checked) }
    }
}