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
import com.pascalwelsch.konduit.ti.BoundView
import com.pascalwelsch.konduit.ti.BuildContext
import com.pascalwelsch.konduit.ti.KonduitActivity
import com.pascalwelsch.konduit.ti.KonduitPresenter
import com.pascalwelsch.konduit.widget.Widget
import com.pascalwelsch.konduit.widget.WidgetListBuilder
import com.pascalwelsch.konduit.widget.button
import com.pascalwelsch.konduit.widget.text
import com.pascalwelsch.konduit.widget.widgetList

class MainActivity : KonduitActivity<MainPresenter, BoundView>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myProgressBar = findViewById<android.widget.ProgressBar>(R.id.progress_bar)
        renderer.bind<ProgressBar>(myProgressBar) { widget ->
            myProgressBar.progress = Math.round(widget.progress * myProgressBar.max)
        }
    }

    override fun providePresenter() = MainPresenter()
}

class MainPresenter : KonduitPresenter<BoundView>() {

    private var count = 0

    private var myProgress = 0f

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
                progress = myProgress
            }
        }
    }

    private val onButtonClicked = {
        setState {
            count++

            // change progress
            myProgress += 0.1f
            if (myProgress > 1) {
                myProgress = 0f
            }
        }
    }
}

class ProgressBar : Widget() {

    var progress: Float = 0f
        set(value) {
            checkWritability()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProgressBar) return false
        if (!super.equals(other)) return false

        if (progress != other.progress) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + progress.hashCode()
        return result
    }
}

private fun WidgetListBuilder.progressBar(init: ProgressBar.() -> Unit): ProgressBar = add(ProgressBar(), init)

