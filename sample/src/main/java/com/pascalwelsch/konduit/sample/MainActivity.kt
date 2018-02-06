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

package com.pascalwelsch.konduit.sample

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AlertDialog.Builder
import com.pascalwelsch.konduit.BuildContext
import com.pascalwelsch.konduit.KonduitActivity
import com.pascalwelsch.konduit.KonduitPresenter
import com.pascalwelsch.konduit.KonduitView
import com.pascalwelsch.konduit.widget.Widget
import com.pascalwelsch.konduit.widget.button
import com.pascalwelsch.konduit.widget.input
import com.pascalwelsch.konduit.widget.progressBar
import com.pascalwelsch.konduit.widget.text
import com.pascalwelsch.konduit.widget.widgetList

class MainActivity : KonduitActivity<MainPresenter, KonduitView>() {

    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        renderer.bind<FriendAlertWidget>("friend dialog") { widget ->
            alertDialog?.setMessage(widget.message)
            alertDialog?.setOnDismissListener { widget.onCancel?.invoke() }
        }
    }

    override fun onWidgetAdded(widget: Widget) {
        super.onWidgetAdded(widget)
        when (widget.key) {
            "friend dialog" -> {
                alertDialog = Builder(this)
                        .setMessage("Welcome Friend")
                        .setPositiveButton("Cool") { d, i ->

                        }
                        .create()
                alertDialog?.show()
            }
        }
    }

    override fun onWidgetRemoved(widget: Widget) {
        super.onWidgetRemoved(widget)

        when (widget.key) {
            "friend dialog" -> {
                alertDialog?.dismiss()
                alertDialog = null
            }
        }
    }

    override fun providePresenter() = MainPresenter()
}

class MainPresenter : KonduitPresenter<KonduitView>() {

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

            if (showFriedDialog) {
                friendAlert {
                    key = "friend dialog"
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

