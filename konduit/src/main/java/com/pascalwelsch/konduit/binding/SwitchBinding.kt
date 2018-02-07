package com.pascalwelsch.konduit.binding

import android.view.View
import android.widget.Switch
import com.pascalwelsch.konduit.ViewBinding
import com.pascalwelsch.konduit.ViewBindingAdapter
import com.pascalwelsch.konduit.widget.SwitchWidget

class SwitchBindingAdapter : ViewBindingAdapter {
    override fun createBinding(view: View, bindWith: (ViewBinding<*>) -> Unit) {
        if (view is Switch) {
            bindWith(SwitchBinding(view))
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
        switch.setOnCheckedChangeListener(null)
        switch.isChecked = widget.checked
        switch.setOnCheckedChangeListener { _, checked -> widget.onSwitch?.invoke(checked) }
    }
}