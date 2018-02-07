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

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AlertDialog.Builder
import com.pascalwelsch.konduit.KonduitActivity
import com.pascalwelsch.konduit.KonduitView
import com.pascalwelsch.konduit.ViewBinding
import com.pascalwelsch.konduit.widget.Widget

class SampleActivity : KonduitActivity<SamplePresenter, KonduitView>() {

    override fun providePresenter() = SamplePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bind(friendDialogKey, FriendAlertBinding(this))
        bind(sampleFragment, object : ViewBinding<Widget> {
            private var fragment: Fragment? = null

            override fun onAdded(widget: Widget) {
                fragment = SampleFragment()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commitAllowingStateLoss()

            }

            override fun onChanged(widget: Widget) {

            }

            override fun onRemoved(widget: Widget) {
                supportFragmentManager.beginTransaction()
                        .remove(fragment)
                        .commitAllowingStateLoss()
            }
        })
    }
}

class FriendAlertBinding(private val context: Context) : ViewBinding<FriendAlertWidget> {

    private var alertDialog: AlertDialog? = null

    override fun onAdded(widget: FriendAlertWidget) {
        alertDialog = Builder(context)
                // set a fake message so the alert dialog adds the message field which
                // will be filled later when binding the widget to the view
                .setMessage("")
                .setPositiveButton("Cool", null)
                .create()
        alertDialog?.show()
    }

    override fun onChanged(widget: FriendAlertWidget) {
        alertDialog?.setMessage(widget.message)
        alertDialog?.setOnDismissListener { widget.onDismiss?.invoke() }
    }

    override fun onRemoved(widget: FriendAlertWidget) {
        alertDialog?.dismiss()
        alertDialog = null
    }
}