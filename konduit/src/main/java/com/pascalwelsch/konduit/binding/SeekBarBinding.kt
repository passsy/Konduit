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
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.pascalwelsch.konduit.ViewBinding
import com.pascalwelsch.konduit.ViewBindingAdapter
import com.pascalwelsch.konduit.widget.SeekBarWidget

class SeekBarBindingAdapter : ViewBindingAdapter {
    override fun createBinding(view: View, bindWith: (ViewBinding<*>) -> Unit) {
        if (view is SeekBar) {
            bindWith(SeekBarBinding(view))
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