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

package com.pascalwelsch.konduit.android

import android.databinding.adapters.TextViewBindingAdapter
import android.text.Editable
import android.widget.TextView
import com.pascalwelsch.konduit.widget.Text
import com.pascalwelsch.konduit.widget.Widget


class TextViewBinding(private val textView: TextView) : AndroidViewBinding {

    override fun bind(widget: Widget) {
        if (widget is Text) {

            TextViewBindingAdapter.setText(textView, widget.text)

            val on = widget.onTextChanged?.let { { text: Editable -> it.invoke(text.toString()) } }
            TextViewBindingAdapter.setTextWatcher(textView, null, null, on, null)

            TextViewBindingAdapter.setMaxLength(textView, widget.maxLength ?: Int.MAX_VALUE)
        }
    }

}