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
import com.pascalwelsch.konduit.test.typeText
import com.pascalwelsch.konduit.widget.ButtonWidget
import com.pascalwelsch.konduit.widget.InputWidget
import com.pascalwelsch.konduit.widget.ProgressBarWidget
import com.pascalwelsch.konduit.widget.TextWidget
import org.assertj.core.api.Assertions.*
import org.junit.*

class SamplePresenterTest {

    @Test
    fun `button click increments`() {
        val ui = SamplePresenter().testUi()

        // Given the UI shows the initial state
        assertThat(ui.widget<TextWidget>(R.id.counter_label).text).contains("0")

        // When clicking the increment button
        ui.widget<ButtonWidget>(R.id.increment).click()

        // Then the counter will be incremented by 1
        assertThat(ui.widget<TextWidget>(R.id.counter_label).text).contains("1")
    }

    @Test
    fun `progress bar jumps to 0 after 10 clicks`() {
        val ui = SamplePresenter().testUi()

        // Given the UI shows the initial state
        assertThat(ui.widget<ProgressBarWidget>(R.id.progress_bar).progress).isEqualTo(0f)

        // When clicking the increment button 10 times
        repeat(10) {
            ui.widget<ButtonWidget>(R.id.increment).click()
        }

        // Then the progress is at maximum
        assertThat(ui.widget<ProgressBarWidget>(R.id.progress_bar).progress).isCloseTo(1f, withinPercentage(1))

        // Another click
        ui.widget<ButtonWidget>(R.id.increment).click()

        // sets it to first step
        assertThat(ui.widget<ProgressBarWidget>(R.id.progress_bar).progress).isEqualTo(0.1f)
    }

    @Test
    fun `show friend dialog when writing friend - 0 clicks`() {
        val ui = SamplePresenter().testUi()

        // Given no friend dialog is not shown
        ui.widgetIsAbsent(friendDialogKey)

        // When the user types friend
        ui.widget<InputWidget>(R.id.text_input).typeText("friend")

        // Then a dialog shows a message animating the user to click increment
        val friendDialog = ui.widget<FriendAlertWidget>(friendDialogKey)
        assertThat(friendDialog.message).isEqualTo(ui.context.getString(R.string.please_click_increment_alert_msg))
    }

    @Test
    fun `show friend dialog when writing friend - multiple clicks`() {
        val ui = SamplePresenter().testUi()

        // Given no friend dialog is not shown
        ui.widgetIsAbsent(friendDialogKey)
        // And the user clicked a few times
        repeat(4) {ui.widget<ButtonWidget>(R.id.increment).click()}
        assertThat(ui.widget<TextWidget>(R.id.counter_label).text).contains("4")

        // When the user types friend
        ui.widget<InputWidget>(R.id.text_input).typeText("friend")

        // Then a dialog shows a message animating the user to click increment
        val friendDialog = ui.widget<FriendAlertWidget>(friendDialogKey)
        assertThat(friendDialog.message).contains("4")
    }

    @Test
    fun `show friend dialog when writing friend at the end`() {
        val ui = SamplePresenter().testUi()

        // Given no friend dialog is not shown
        ui.widgetIsAbsent(friendDialogKey)

        // When the user types friend
        ui.widget<InputWidget>(R.id.text_input).typeText("hello friend")

        // Then a dialog shows a message animating the user to click increment
        val friendDialog = ui.widget<FriendAlertWidget>(friendDialogKey)
        assertThat(friendDialog.message).isEqualTo(ui.context.getString(R.string.please_click_increment_alert_msg))
    }

    @Test
    fun `dismiss friend dialog`() {
        val ui = SamplePresenter().testUi()

        // Given the friend dialog is shown
        ui.widget<InputWidget>(R.id.text_input).typeText("friend")
        val friendDialog = ui.widget<FriendAlertWidget>(friendDialogKey)

        // When the user dismisses the dialog (by pressing back or the positive button)
        friendDialog.onDismiss?.invoke()

        // Then the dialog will be removed
        ui.widgetIsAbsent(friendDialogKey)
    }

}