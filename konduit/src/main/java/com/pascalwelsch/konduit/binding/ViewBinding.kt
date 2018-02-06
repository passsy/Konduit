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

import android.view.View
import android.widget.AdapterView
import com.pascalwelsch.konduit.widget.Widget

class ViewBinding(private val view: View) : AndroidViewBinding {
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