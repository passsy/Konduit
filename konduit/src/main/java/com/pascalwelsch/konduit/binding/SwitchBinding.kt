package com.pascalwelsch.konduit.binding

import android.view.View
import android.widget.Switch
import com.pascalwelsch.konduit.ViewBinding
import com.pascalwelsch.konduit.ViewBindingAdapters
import com.pascalwelsch.konduit.widget.SwitchWidget

class SwitchBindingBindingAdapters : ViewBindingAdapters {
    override fun createBinding(view: View, emit: (ViewBinding<*>) -> Unit) {
        if (view is Switch) {
            emit(SwitchBinding(view))
        }
    }
}

private class SwitchBinding(private val switch: Switch) : ViewBinding<SwitchWidget> {

    private var initialState: SwitchWidget? = null

    override fun onAdded(widget: SwitchWidget) {
        initialState = SwitchWidget().apply {
            checked = switch.isChecked
        }
    }

    override fun onRemoved(widget: SwitchWidget) {
        initialState?.let { onChanged(it) }
    }

    override fun onChanged(widget: SwitchWidget) {
        if (widget !is SwitchWidget) return

        switch.setOnCheckedChangeListener(null)
        switch.isChecked = widget.checked
        switch.setOnCheckedChangeListener { _, checked -> widget.onSwitch?.invoke(checked) }
    }
}