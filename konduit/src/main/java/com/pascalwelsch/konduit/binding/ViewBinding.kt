/*
 * Copyright (C) 2017 Pascal Welsch
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
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.pascalwelsch.konduit.ViewBinding
import com.pascalwelsch.konduit.ViewBindingAdapter
import com.pascalwelsch.konduit.widget.Widget

class ViewBindingAdapter : ViewBindingAdapter {
    override fun createBinding(view: View, bindWith: (ViewBinding<*>) -> Unit) = bindWith(ViewBinding(view))
}

private class ViewBinding(private val view: View) : ViewBinding<Widget> {

    private var initialState: Widget? = null

    override fun onAdded(widget: Widget) {
        // save initial view state
        initialState = Widget().apply {
            enabled = view.isEnabled
            visible = view.visibility == View.VISIBLE
            // can't restore click listener, would require reflection
            //onClick = view.getOnClickListener()
            // at least restore isClickable by setting a fake listener if one is set
            if (view.isClickable && (VERSION.SDK_INT < VERSION_CODES.ICE_CREAM_SANDWICH_MR1 || view.hasOnClickListeners())) {
                onClick = {
                    // add fake click listener to at least restore the isClickable UI state
                    Log.e("Konduit", "Missing listener! For some period $view was bound to a widget. " +
                            "The widget is now removed but the old click listener could not be recovered. " +
                            "Make sure to manually restore the click functionality in onRemoved().")
                }
            }
        }
    }

    override fun onRemoved(widget: Widget) {
        // restore initial state
        initialState?.let { onChanged(it) }
    }

    override fun onChanged(widget: Widget) {
        if (view.isEnabled != widget.enabled) {
            view.isEnabled = widget.enabled
        }
        if (view.visibility == View.VISIBLE != widget.visible) {
            view.visibility = if (widget.visible) View.VISIBLE else View.GONE
        }

        if (view !is AdapterView<*>) {
            val clickListener = widget.onClick?.let { click -> View.OnClickListener { click.invoke() } }
            view.setOnClickListener(clickListener)
            view.isClickable = clickListener != null
        }
    }
}