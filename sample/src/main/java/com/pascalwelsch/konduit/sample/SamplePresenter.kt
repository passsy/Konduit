package com.pascalwelsch.konduit.sample

import com.pascalwelsch.konduit.BuildContext
import com.pascalwelsch.konduit.KonduitPresenter
import com.pascalwelsch.konduit.KonduitView
import com.pascalwelsch.konduit.sample.R.id
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
                key = id.counter_label
                text = "Clicked $count times"
            }

            button {
                key = id.increment
                text = if (count == 0) "Click me" else "Increment"
                onClick = onButtonClicked
            }

            progressBar {
                key = id.progress_bar
                progress = myProgress / 10f
            }

            input {
                key = id.text_input
                hint = "Write 'friend' and see magic happen"
                text = userInput
                onTextChanged = onInputTextChanged
            }

            if (showFriedDialog) {
                friendAlert {
                    key = friendDialogKey
                    message = if (count > 0) {
                        "Thanks for clicking $count times, friend!"
                    } else {
                        "Click INCREMENT first!"
                    }
                    onCancel = {
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
            if ("friend" == userInput) {
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

