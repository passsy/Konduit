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

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import android.widget.ProgressBar
import com.pascalwelsch.konduit.ViewBinding
import com.pascalwelsch.konduit.ViewBindingAdapter
import com.pascalwelsch.konduit.widget.ProgressBarWidget

class ProgressBarBindingAdapter : ViewBindingAdapter {
    override fun createBinding(view: View, bindWith: (ViewBinding<*>) -> Unit) {
        if (view is ProgressBar) {
            bindWith(ProgressBarBinding(view))
        }
    }
}

private class ProgressBarBinding(private val progressBar: ProgressBar) : ViewBinding<ProgressBarWidget> {

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