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

package com.pascalwelsch.konduit.sample.alert

import android.os.Bundle
import com.pascalwelsch.konduit.BuildContext
import com.pascalwelsch.konduit.KonduitActivity
import com.pascalwelsch.konduit.KonduitPresenter
import com.pascalwelsch.konduit.KonduitView
import com.pascalwelsch.konduit.sample.R
import com.pascalwelsch.konduit.sample.R.layout
import com.pascalwelsch.konduit.widget.Widget
import com.pascalwelsch.konduit.widget.button
import com.pascalwelsch.konduit.widget.widgetList

class AlertActivity : KonduitActivity<AlertPresenter, KonduitView>() {

    override fun providePresenter() = AlertPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_alert)
        title = "FizzBuzz"

        // Konduit doesn't know how to bind a 
        bind(fizzBuzzAlert, FizzBuzzAlertBinding(this))
    }
}

internal const val fizzBuzzAlert = "alert"

class AlertPresenter : KonduitPresenter<KonduitView>() {

    private var counter = 0

    private var showFriedDialog = false

    override fun build(context: BuildContext): List<Widget> = widgetList {

        button {
            key = R.id.click_me_btn
            text = if (counter == 0) "Click me" else counter.toString()
            onClick = ::onButtonClicked
        }

        // show the dialog as long as `showFriedDialog` is true, when the fizzBuzzAlert Widget is absent it will
        // be removed from the UI (`onRemoved` gets called)
        if (showFriedDialog) {

            fizzBuzzAlert {
                key = fizzBuzzAlert
                message = "$counter -> ${counter.toFizzBuzz()}"

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

    private fun onButtonClicked() {
        setState {
            counter++

            if (counter.toFizzBuzz() != counter.toString()) {
                showFriedDialog = true
            }
        }
    }

    private fun Int.toFizzBuzz(): String {
        fun Int.isBuzz() = rem(5) == 0 || toString().contains("5")
        fun Int.isFizz() = rem(3) == 0 || toString().contains("3")

        return when {
            isFizz() && isBuzz() -> "FizzBuzz"
            isFizz() -> "Fizz"
            isBuzz() -> "Buzz"
            else -> toString()
        }
    }
}
