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

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.app.AlertDialog.Builder
import com.pascalwelsch.konduit.ViewBinding

class FizzBuzzAlertBinding(private val context: Context) : ViewBinding<FizzBuzzAlertWidget> {

    private var alertDialog: AlertDialog? = null

    override fun onAdded(widget: FizzBuzzAlertWidget) {
        alertDialog = Builder(context)
                // set a fake message so the alert dialog adds the message field which
                // will be filled later when binding the widget to the view
                .setMessage("")
                .setPositiveButton("Cool", null)
                .create()
        alertDialog?.show()
    }

    override fun onChanged(widget: FizzBuzzAlertWidget) {
        alertDialog?.setMessage(widget.message)
        alertDialog?.setOnDismissListener { widget.onDismiss?.invoke() }
    }

    override fun onRemoved(widget: FizzBuzzAlertWidget) {
        alertDialog?.dismiss()
        alertDialog = null
    }
}
