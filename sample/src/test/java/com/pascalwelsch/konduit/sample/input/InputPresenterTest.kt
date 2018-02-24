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

package com.pascalwelsch.konduit.sample.input

import com.pascalwelsch.konduit.sample.R
import com.pascalwelsch.konduit.test.click
import com.pascalwelsch.konduit.test.testUi
import com.pascalwelsch.konduit.test.typeText
import com.pascalwelsch.konduit.widget.ButtonWidget
import com.pascalwelsch.konduit.widget.InputWidget
import com.pascalwelsch.konduit.widget.TextWidget
import org.assertj.core.api.Assertions.*
import org.junit.*

class InputPresenterTest {
    private val ui = InputPresenter().testUi()

    // define UI elements for faster access. Always use calculated properties, the Widgets
    // change whenever the build method is called
    private val input get() = ui.widget<InputWidget>(R.id.input_field)
    private val counterLabel get() = ui.widget<TextWidget>(R.id.charCount)
    private val clearButton get() = ui.widget<ButtonWidget>(R.id.clear_button)

    @Test
    fun `default state is empty with hint text`() {
        assertThat(counterLabel.text).isEqualTo("length: 0")
        assertThat(input.text).isEqualTo("")
        assertThat(input.hint).isEqualTo(ui.context.getString(R.string.hint_type_something))
    }

    @Test
    fun `typing text increments the counter`() {
        input.typeText("Hello World")
        assertThat(counterLabel.text).isEqualTo("length: 11")
        input.typeText("Moin!")
        assertThat(counterLabel.text).isEqualTo("length: 5")
    }

    @Test
    fun `clear resets text`() {
        input.typeText("Hello World")
        assertThat(counterLabel.text).isEqualTo("length: 11")

        clearButton.click()

        assertThat(input.text).isEqualTo("")
        assertThat(input.hint).isEqualTo(ui.context.getString(R.string.hint_type_something))
        assertThat(counterLabel.text).isEqualTo("length: 0")
    }
}