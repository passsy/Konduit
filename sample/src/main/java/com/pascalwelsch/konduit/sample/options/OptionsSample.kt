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

package com.pascalwelsch.konduit.sample.options

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.pascalwelsch.konduit.BuildContext
import com.pascalwelsch.konduit.KonduitActivity
import com.pascalwelsch.konduit.KonduitPresenter
import com.pascalwelsch.konduit.KonduitView
import com.pascalwelsch.konduit.sample.R
import com.pascalwelsch.konduit.widget.Widget
import com.pascalwelsch.konduit.widget.checkBox
import com.pascalwelsch.konduit.widget.widget
import com.pascalwelsch.konduit.widget.widgetList

class AcceptTosActivity : KonduitActivity<OptionsMenuPresenter, AcceptTosView>(), AcceptTosView {

    private var onSubmitWidget: Widget? = null

    override fun providePresenter() = OptionsMenuPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options_menu)

        // The options menu is not always there and directly accessing the items is difficult. Therefore the view
        // saves the latest widget, triggers a rebuild and binds the data when the value is ready
        bind<Widget>(R.id.submit, onChange = {
            // save latest widget
            onSubmitWidget = it
            // trigger onPrepareOptionsMenu
            invalidateOptionsMenu()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.tos_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.submit).apply {
            // bind the options menu widget in both directions
            isEnabled = onSubmitWidget?.enabled ?: false
            setOnMenuItemClickListener(onSubmitWidget?.onClick?.let { listener ->
                MenuItem.OnMenuItemClickListener menu@{ listener.invoke(); return@menu true }
            })
        }
        return true
    }

    override fun showSuccessToast() {
        Toast.makeText(this, "Successful submitted", Toast.LENGTH_SHORT).show()
    }
}

interface AcceptTosView : KonduitView {
    fun showSuccessToast()
}

class OptionsMenuPresenter : KonduitPresenter<AcceptTosView>() {

    private var tosAccepted = false

    override fun build(context: BuildContext): List<Widget> = widgetList {
        checkBox {
            key = R.id.tos_accepted
            enabled = true
            checked = tosAccepted
            onCheckedChanged = ::onCheckTos
            text = "Accept Terms or service"
        }

        widget {
            key = R.id.submit
            enabled = tosAccepted
            onClick = ::onSubmit
        }
    }

    private fun onSubmit() {
        // reset view
        setState {
            tosAccepted = false
        }
        // Toasts are a volatile view state and do not require a state property in the Presenter
        viewOrThrow.showSuccessToast()
    }

    private fun onCheckTos(checked: Boolean) {
        setState {
            tosAccepted = checked
        }
    }
}