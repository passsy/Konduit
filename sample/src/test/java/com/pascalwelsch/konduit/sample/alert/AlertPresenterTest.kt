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

import com.pascalwelsch.konduit.sample.R
import com.pascalwelsch.konduit.test.click
import com.pascalwelsch.konduit.test.testUi
import com.pascalwelsch.konduit.widget.ButtonWidget
import org.assertj.core.api.Assertions.*
import org.junit.*

class AlertPresenterTest {

    private val ui = AlertPresenter().testUi()

    // define UI elements for faster access. Always use calculated properties, the Widgets
    // change whenever the build method is called
    private val button get() = ui.widget<ButtonWidget>(R.id.click_me_btn)
    private val alert get() = ui.widgetOrNull<FizzBuzzAlertWidget>(fizzBuzzAlert)

    @Test
    fun `click increments the counter`() {
        button.click()
        assertThat(button.text).isEqualTo("1")
        button.click()
        assertThat(button.text).isEqualTo("2")
    }

    @Test
    fun `fizz alert is shows after 3 clicks`() {
        repeat(3) { button.click() }
        assertThat(button.text).isEqualTo("3")
        assertThat(alert!!.message).isEqualTo("3 -> Fizz")
    }

    @Test
    fun `buzz alert is shows after 5 clicks`() {
        repeat(4) { button.click(); alert?.dismiss() }
        button.click()
        assertThat(alert!!.message).isEqualTo("5 -> Buzz")
    }

    @Test
    fun `15 clicks show FizzBuzz`() {
        repeat(14) {
            button.click()
            alert?.dismiss()
        }
        button.click()
        assertThat(alert!!.message).isEqualTo("15 -> FizzBuzz")
    }

    @Test
    fun `dismiss closes the dialog`() {
        repeat(3) { button.click() }
        assertThat(alert!!.message).isEqualTo("3 -> Fizz")

        // dismiss removes the dialog
        alert!!.dismiss()
        assertThat(alert).isNull()
    }
}

private fun FizzBuzzAlertWidget.dismiss() {
    onDismiss?.invoke()
}
