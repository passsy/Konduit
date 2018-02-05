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

import com.pascalwelsch.konduit.test.click
import com.pascalwelsch.konduit.test.testUi
import com.pascalwelsch.konduit.widget.Button
import com.pascalwelsch.konduit.widget.Text
import org.assertj.core.api.Assertions.*
import org.junit.*

class MainPresenterTest {

    @Test
    fun `button click increments`() {
        val ui = MainPresenter().testUi()

        // Given the UI shows the initial state
        assertThat(ui.widget<Text>(R.id.counter_label).text).contains("0")

        // When clicking the increment button
        ui.widget<Button>(R.id.increment).click()

        // Then the counter will be incremented by 1
        assertThat(ui.widget<Text>(R.id.counter_label).text).contains("1")
    }
}