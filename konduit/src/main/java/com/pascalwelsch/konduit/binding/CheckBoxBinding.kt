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
import android.widget.CheckBox
import android.widget.CompoundButton
import com.pascalwelsch.konduit.ViewBinding
import com.pascalwelsch.konduit.ViewBindingAdapter
import com.pascalwelsch.konduit.widget.CheckBoxWidget

class CheckBoxBindingAdapter : ViewBindingAdapter {
    override fun createBinding(view: View, bindWith: (ViewBinding<*>) -> Unit) {
        if (view is CheckBox) {
            bindWith(CheckBoxBinding(view))
        }
    }
}

private class CheckBoxBinding(private val checkBox: CheckBox) : ViewBinding<CheckBoxWidget> {

    private var initialState: CheckBoxWidget? = null

    override fun onAdded(widget: CheckBoxWidget) {
        initialState = CheckBoxWidget().apply {
            checked = checkBox.isChecked
        }
    }

    override fun onRemoved(widget: CheckBoxWidget) {
        initialState?.let { onChanged(it) }
    }

    override fun onChanged(widget: CheckBoxWidget) {
        checkBox.isChecked = widget.checked

        checkBox.isClickable = widget.onCheckedChanged != null || widget.onClick != null

        checkBox.setOnCheckedChangeListener(widget.onCheckedChanged?.let { listener ->
            CompoundButton.OnCheckedChangeListener { _, isChecked ->
                listener.invoke(isChecked)
            }
        })
    }
}