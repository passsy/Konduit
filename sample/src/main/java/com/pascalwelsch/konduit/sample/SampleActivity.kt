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
import com.pascalwelsch.konduit.KonduitActivity
import com.pascalwelsch.konduit.KonduitView
import com.pascalwelsch.konduit.widget.Widget

class SampleActivity : KonduitActivity<SamplePresenter, KonduitView>() {

    private var alertDialog: AlertDialog? = null

    override fun providePresenter() = SamplePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        renderer.bind<FriendAlertWidget>(friendDialogKey) { widget ->
            alertDialog?.setMessage(widget.message)
            alertDialog?.setOnDismissListener { widget.onDismiss?.invoke() }
        }
    }

    override fun onWidgetAdded(widget: Widget) {
        super.onWidgetAdded(widget)
        when (widget.key) {
            friendDialogKey -> {
                alertDialog = Builder(this)
                        // set a fake message so the alert dialog adds the message field which
                        // will be filled later when binding the widget to the view
                        .setMessage("")
                        .setPositiveButton("Cool", null)
                        .create()
                alertDialog?.show()
            }
        }
    }

    override fun onWidgetRemoved(widget: Widget) {
        super.onWidgetRemoved(widget)

        when (widget.key) {
            friendDialogKey -> {
                alertDialog?.dismiss()
                alertDialog = null
            }
        }
    }
}
