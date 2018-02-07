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

package com.pascalwelsch.konduit.widget

import org.assertj.core.api.Assertions.*
import org.junit.*

class WidgetTest {

    @Test
    fun `equal when no property is set`() {
        val w1 = Widget()
        val w2 = Widget()

        assertThat(w1).isEqualTo(w2).isNotSameAs(w2)
    }

    @Test
    fun `not equal for different key`() {
        val w1 = Widget().apply { key = 1 }
        val w2 = Widget().apply { key = 2 }

        assertThat(w1).isNotEqualTo(w2)
    }

    @Test
    fun `not equal for different property 'visible'`() {
        val w1 = Widget().apply { visible = true }
        val w2 = Widget().apply { visible = false }

        assertThat(w1).isNotEqualTo(w2)
    }

    @Test
    fun `not equal for different click action lambdas`() {
        // different lambda instances
        val w1 = Widget().apply { onClick = {} }
        val w2 = Widget().apply { onClick = {} }

        assertThat(w1).isNotEqualTo(w2)
    }

    @Test
    fun `equal for same click action lambdas`() {

        val myOnClick = {}
        // different lambda instances
        val w1 = Widget().apply { onClick = myOnClick }
        val w2 = Widget().apply { onClick = myOnClick }

        assertThat(w1).isEqualTo(w2)
    }

    @Test
    fun `changing properties is not possible after locking`() {
        val w = Widget()
        w.lock()

        val expKey = catchThrowable { w.key = false }
        assertThat(expKey).isInstanceOf(IllegalStateException::class.java)

        val expEnabled = catchThrowable { w.enabled = false }
        assertThat(expEnabled).isInstanceOf(IllegalStateException::class.java)

        val expVisible = catchThrowable { w.visible = false }
        assertThat(expVisible).isInstanceOf(IllegalStateException::class.java)

        val expClick = catchThrowable { w.onClick = {} }
        assertThat(expClick).isInstanceOf(IllegalStateException::class.java)
    }
}

