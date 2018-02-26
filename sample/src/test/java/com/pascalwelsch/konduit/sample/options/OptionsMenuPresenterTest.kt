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

import com.nhaarman.mockito_kotlin.*
import com.pascalwelsch.konduit.sample.R
import com.pascalwelsch.konduit.test.check
import com.pascalwelsch.konduit.test.click
import com.pascalwelsch.konduit.test.testUi
import com.pascalwelsch.konduit.widget.CheckBoxWidget
import com.pascalwelsch.konduit.widget.Widget
import org.assertj.core.api.Assertions.*
import org.junit.*

class OptionsMenuPresenterTest {

    private val ui = OptionsMenuPresenter().testUi()

    // define UI elements for faster access. Always use calculated properties, the Widgets
    // change whenever the build method is called
    private val acceptCheckBox get() = ui.widget<CheckBoxWidget>(R.id.tos_accepted)
    private val submitButton get() = ui.widget<Widget>(R.id.submit)

    @Test
    fun `default - submit disabled, accept unchecked`() {
        assertThat(acceptCheckBox.enabled).isTrue()
        assertThat(acceptCheckBox.checked).isFalse()

        assertThat(submitButton.enabled).isFalse()
    }

    @Test
    fun `click accept to enable click`() {
        acceptCheckBox.check()

        assertThat(submitButton.enabled).isTrue()
    }

    @Test
    fun `submit shows toast`() {
        acceptCheckBox.check()
        submitButton.click()

        verify(ui.view).showSuccessToast()
    }
}