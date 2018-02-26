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

package com.pascalwelsch.konduit.sample.input

import android.os.Bundle
import com.pascalwelsch.konduit.BuildContext
import com.pascalwelsch.konduit.KonduitActivity
import com.pascalwelsch.konduit.KonduitPresenter
import com.pascalwelsch.konduit.KonduitView
import com.pascalwelsch.konduit.sample.R
import com.pascalwelsch.konduit.widget.Widget
import com.pascalwelsch.konduit.widget.button
import com.pascalwelsch.konduit.widget.input
import com.pascalwelsch.konduit.widget.text
import com.pascalwelsch.konduit.widget.widgetList

/**
 * EditText is special because it requires a two way binding. It has to react on user input changes and on
 * data changes in the presenter. This is tricky because data changes should not result in a user input change.
 */
class InputActivity : KonduitActivity<InputPresenter, KonduitView>() {

    override fun providePresenter() = InputPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        // Notice that no glue code is required, Konduit knows how to bind standard Android widgets to
        // Konduit Widgets. This works automatically when the Widget key is the R.id of the Android Widget.
    }
}

class InputPresenter : KonduitPresenter<KonduitView>() {

    // This is the only state in this Activity. It survives configuration changes together with the presenter
    private var inputText: String = ""

    override fun build(context: BuildContext): List<Widget> = widgetList {
        input {
            key = R.id.input_field
            // when the text changes, onTextChanged(text: String) gets called
            onTextChanged = ::onTextChanged
            text = inputText
            hint = context.getString(R.string.hint_type_something)
        }

        text {
            key = R.id.charCount
            text = "length: ${inputText.length}"
        }

        button {
            key = R.id.clear_button
            text = context.getString(R.string.clear)
            // bind callbacks with function references for better performance. Two lambdas (even with the same code)
            // aren't equal and would result in an unnecessary binding.
            onClick = ::onClearClicked
        }
    }

    private fun onTextChanged(text: String) {
        // setState triggers the build method which rebuilds the widgetList and binds it to the View
        setState {
            inputText = text
        }
    }

    private fun onClearClicked() {
        // clearing the text doesn't work by explizitly calling .clear() on the EditText. Only the data changes,
        // the binding happens automatically
        setState {
            inputText = ""
        }
    }
}