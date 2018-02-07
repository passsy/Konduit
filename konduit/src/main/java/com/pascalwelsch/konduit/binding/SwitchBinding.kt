/*
 * Copyright (C) 2018 Pascal Welsch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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