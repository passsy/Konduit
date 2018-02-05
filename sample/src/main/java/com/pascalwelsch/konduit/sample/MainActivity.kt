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
import com.pascalwelsch.konduit.BuildContext
import com.pascalwelsch.konduit.KonduitActivity
import com.pascalwelsch.konduit.KonduitPresenter
import com.pascalwelsch.konduit.KonduitView
import com.pascalwelsch.konduit.widget.Widget
import com.pascalwelsch.konduit.widget.button
import com.pascalwelsch.konduit.widget.progressBar
import com.pascalwelsch.konduit.widget.text
import com.pascalwelsch.konduit.widget.widgetList

class MainActivity : KonduitActivity<MainPresenter, KonduitView>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun providePresenter() = MainPresenter()
}

class MainPresenter : KonduitPresenter<KonduitView>() {

    private var count = 0

    private var myProgress = 0

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

