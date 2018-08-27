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

package com.pascalwelsch.konduit

import com.pascalwelsch.konduit.test.testUi
import com.pascalwelsch.konduit.widget.Widget
import com.pascalwelsch.konduit.widget.widget
import com.pascalwelsch.konduit.widget.widgetList
import org.assertj.core.api.Assertions.*
import org.junit.*

class KonduitPresenterTest {

    class MyPresenter : KonduitPresenter<KonduitView>() {

        var mockState: (() -> List<Widget>) = { emptyList() }
            set(value) {
                field = value
                dispatchRender()
            }

        override fun build(context: BuildContext): List<Widget> = mockState()
    }

    @Test
    fun `render twice does not update view when widgets don't change`() {
        val p = MyPresenter()
        p.mockState = {
            widgetList {
                widget {
                }
            }
        }

        // start observing the UI
        val ui = p.testUi()

        // receive that change in the UI
        assertThat(ui.captor.lastValue).satisfies {
            assertThat(it).hasSize(1)
            assertThat(it).isEqualTo(listOf(Widget().apply { lock() }))
        }

        // only one render of the UI is expected
        assertThat(ui.captor.allValues).hasSize(1)

        // When rendering again
        p.dispatchRender()

        // Then render in not called again
        assertThat(ui.captor.allValues).hasSize(1)
    }

    @Test
    fun `changing a widget does update the view`() {
        val p = MyPresenter()
        p.mockState = {
            widgetList {
                widget {
                    visible = true
                }
            }
        }

        // start observing the UI
        val ui = p.testUi()

        // receive that change in the UI
        assertThat(ui.captor.lastValue).satisfies {
            assertThat(it).hasSize(1)
            assertThat(it.first()).isEqualTo(Widget().apply {
                lock()
            })
        }

        // only one render of the UI is expected
        assertThat(ui.captor.allValues).hasSize(1)

        // When the widget changes
        p.mockState = {
            widgetList {
                widget {
                    visible = false
                }
            }
        }

        // Then the view was rendered again
        assertThat(ui.captor.allValues).hasSize(2)
        assertThat(ui.captor.lastValue).satisfies {
            assertThat(it).hasSize(1)
            assertThat(it.first()).isEqualTo(Widget().apply {
                visible = false
                lock()
            })
        }
    }

    @Test
    fun `changing the lambda doesn't update the view, only the reference`() {
        var a = 0
        var b = 0
        val p = MyPresenter()
        p.mockState = {
            widgetList {
                widget {
                    onClick = { a++ }
                }
            }
        }

        // start observing the UI
        val ui = p.testUi()

        // only one render of the UI is expected
        assertThat(ui.captor.allValues).hasSize(1)

        // When clicking the view
        ui.captor.lastValue.first().onClick!!.invoke()

        // Then a is incremented
        assertThat(a).isEqualTo(1)
        assertThat(b).isEqualTo(0)

        // When the lambda changes
        p.mockState = {
            widgetList {
                widget {
                    onClick = { b++ }
                }
            }
        }
        // Then render in not called again
        assertThat(ui.captor.allValues).hasSize(1)

        // When clicking the view again
        ui.captor.lastValue.first().onClick!!.invoke()

        // Then b is incremented
        assertThat(a).isEqualTo(1)
        assertThat(b).isEqualTo(1)
    }
}