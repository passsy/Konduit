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

package com.pascalwelsch.konduit.binding

import com.pascalwelsch.konduit.widget.Widget

/**
 * Will be called to onChanged a widget to an arbitrary android view. This is the only connection between the two worlds
 */
interface AndroidViewBinding {

    /**
     * Called when the [Widget] first appears, should show or initialize the View which should be mapped to this [Widget]
     * [onChanged] will be called directly afterwards
     */
    fun onAdded(widget: Widget) {
        // noop
    }

    /**
     * Called when the [Widget] changes, apply the new values to the bound View
     */
    fun onChanged(widget: Widget)

    /**
     * The [Widget] was removed, also remove the View
     */
    fun onRemoved(widget: Widget) {
        // noop
    }
}