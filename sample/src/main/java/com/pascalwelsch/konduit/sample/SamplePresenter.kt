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

package com.pascalwelsch.konduit.sample

import com.pascalwelsch.konduit.BuildContext
import com.pascalwelsch.konduit.KonduitPresenter
import com.pascalwelsch.konduit.KonduitView
import com.pascalwelsch.konduit.widget.Widget
import com.pascalwelsch.konduit.widget.button
import com.pascalwelsch.konduit.widget.input
import com.pascalwelsch.konduit.widget.progressBar
import com.pascalwelsch.konduit.widget.text
import com.pascalwelsch.konduit.widget.widgetList

internal const val friendDialogKey = "friend dialog"

class SamplePresenter : KonduitPresenter<KonduitView>() {

    private var count = 0

    private var myProgress = 0

    private var userInput = ""

    private var showFriedDialog = false

    override fun build(context: BuildContext): List<Widget> {
        return widgetList {

            text {
                key = R.id.counter_label
                text = "Clicked $count times"
            }

            button {
                key = R.id.increment
                text = if (count == 0) "Click me" else "Increment"
                onClick = onButtonClicked
            }

            progressBar {
                key = R.id.progress_bar
                progress = myProgress / 10f
            }

            input {
                key = R.id.text_input
                hint = "Write 'friend' and see magic happen"
                text = userInput
                onTextChanged = onInputTextChanged
            }

            text {
                key = R.id.text_length
                text = userInput.count().toString()
            }

            if (showFriedDialog) {
                friendAlert {
                    key = friendDialogKey
                    message = if (count > 0) {
                        // either write the text manually
                        "Thanks for clicking $count times, friend!"
                    } else {
                        // or get it from the resources using the BuildContext
                        context.getString(R.string.click_button_message)
                    }
                    // custom callback. Also notice the inline lambda. It is not ideal because this lambda
                    // will make the widget never be equal to any other widget and therefore the
                    // onChanged method will always called whenever build(BuildContext) is called.
                    onDismiss = {
                        setState {
                            showFriedDialog = false
                        }
                    }
                }
            }
        }
    }

    private val onInputTextChanged = { text: String ->
        setState {
            userInput = text
            if (userInput.endsWith("friend")) {
                showFriedDialog = true
            }
        }
    }

    private val onButtonClicked = {
        setState {
            count++

            // change progress
            if (myProgress >= 10) {
                myProgress = 0
            }
            myProgress += 1
        }
    }
}

