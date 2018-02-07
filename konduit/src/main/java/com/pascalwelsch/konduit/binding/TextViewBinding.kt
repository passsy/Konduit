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

import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.SparseArray
import android.view.View
import android.widget.TextView
import com.pascalwelsch.konduit.R
import com.pascalwelsch.konduit.ViewBinding
import com.pascalwelsch.konduit.ViewBindingAdapters
import com.pascalwelsch.konduit.widget.TextWidget
import java.lang.ref.WeakReference
import java.util.WeakHashMap

class TextViewBindingBindingAdapters : ViewBindingAdapters {
    override fun createBinding(view: View, emit: (ViewBinding<*>) -> Unit) {
        if (view is TextView) {
            emit(TextViewBinding(view))
        }
    }
}

@Suppress("UNCHECKED_CAST")
private class TextViewBinding(private val textView: TextView) : ViewBinding<TextWidget> {

    private var initialText: CharSequence? = null
    private var initialHint: CharSequence? = null
    private var initialFilters: Array<InputFilter>? = null

    override fun onAdded(widget: TextWidget) {
        initialText = textView.text
        initialHint = textView.hint
        // save maxLength and more with filters
        initialFilters = textView.filters.copyOf()
    }

    override fun onRemoved(widget: TextWidget) {
        textView.text = initialText
        textView.hint = initialHint
        // setting it to null removes the watcher
        textView.setTextWatcher(after = null)

        textView.filters = initialFilters
    }

    override fun onChanged(widget: TextWidget) {
        textView.setTextWhenChanged(widget.text)
        textView.setHintTextWhenChanged(widget.hint)

        val onChangeListener = widget.onTextChanged?.let { { text: Editable -> it.invoke(text.toString()) } }
        textView.setTextWatcher(after = onChangeListener)

        textView.setMaxLength(widget.maxLength ?: Int.MAX_VALUE)
    }

    // Helper functions from android.databinding.adapters.TextViewBindingAdapter

    private fun TextView.setHintTextWhenChanged(hint: CharSequence?) {
        val oldHint = this.hint
        if (hint === oldHint || hint == null && oldHint.length == 0) {
            return
        }
        if (hint is Spanned) {
            if (hint == oldHint) {
                return  // No change in the spans, so don't set anything.
            }
        } else if (!haveContentsChanged(hint, oldHint)) {
            return  // No content changes, so don't set anything.
        }
        this.hint = hint
    }

    private fun TextView.setTextWhenChanged(text: CharSequence?) {
        val oldText = this.text
        if (text === oldText || text == null && oldText.length == 0) {
            return
        }
        if (text is Spanned) {
            if (text == oldText) {
                return  // No change in the spans, so don't set anything.
            }
        } else if (!haveContentsChanged(text, oldText)) {
            return  // No content changes, so don't set anything.
        }
        this.text = text
    }

    private fun haveContentsChanged(str1: CharSequence?, str2: CharSequence?): Boolean {
        if (str1 == null != (str2 == null)) {
            return true
        } else if (str1 == null) {
            return false
        }
        val length = str1.length
        if (length != str2!!.length) {
            return true
        }
        for (i in 0 until length) {
            if (str1[i] != str2[i]) {
                return true
            }
        }
        return false
    }

    private fun TextView.setTextWatcher(
            before: ((CharSequence, start: Int, count: Int, after: Int) -> Unit)? = null,
            on: ((CharSequence, start: Int, before: Int, count: Int) -> Unit)? = null,
            after: ((Editable) -> Unit)? = null,
            textAttrChanged: (() -> Unit)? = null) {
        val newValue: TextWatcher?
        if (before == null && after == null && on == null && textAttrChanged == null) {
            newValue = null
        } else {
            newValue = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    before?.invoke(s, start, count, after)
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    on?.invoke(s, start, before, count)
                    textAttrChanged?.invoke()
                }

                override fun afterTextChanged(s: Editable) {
                    after?.invoke(s)
                }
            }
        }
        val oldValue = trackListener(this, newValue, R.id.textWatcher)
        if (oldValue != null) {
            removeTextChangedListener(oldValue)
        }
        if (newValue != null) {
            addTextChangedListener(newValue)
        }
    }

    private fun TextView.getMaxLength(): Int? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return null
        return (filters.first { it is InputFilter.LengthFilter } as InputFilter.LengthFilter).max
    }

    private fun TextView.setMaxLength(value: Int) {
        var filters: Array<InputFilter>? = filters
        if (filters == null) {
            filters = arrayOf(InputFilter.LengthFilter(value))
        } else {
            var foundMaxLength = false
            for (i in filters.indices) {
                val filter = filters[i]
                if (filter is InputFilter.LengthFilter) {
                    foundMaxLength = true
                    var replace = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        replace = filter.max != value
                    }
                    if (replace) {
                        filters[i] = InputFilter.LengthFilter(value)
                    }
                    break
                }
            }
            if (!foundMaxLength) {
                filters = filters.copyOf() + InputFilter.LengthFilter(value)
            }
        }
        this.filters = filters
    }

    private val globalListeners = SparseArray<WeakHashMap<View, WeakReference<*>>>()

    /**
     * This method tracks listeners for a View. Only one listener per listenerResourceId
     * can be tracked at a time. This is useful for add*Listener and remove*Listener methods
     * when used with BindingAdapters. This guarantees not to leak the listener or the View,
     * so will not keep a strong reference to either.
     *
     * Example usage:
     * <pre>`@BindingAdapter("onFoo")
     * public static void addFooListener(MyView view, OnFooListener listener) {
     * OnFooListener oldValue = ListenerUtil.trackListener(view, listener, R.id.fooListener);
     * if (oldValue != null) {
     * view.removeOnFooListener(oldValue);
     * }
     * if (listener != null) {
     * view.addOnFooListener(listener);
     * }
     * }`</pre>
     *
     * @param view The View that will have this listener
     * @param listener The listener to keep track of. May be null if the listener is being removed.
     * @param listenerResourceId A unique resource ID associated with the listener type.
     * @return The previously tracked listener. This will be null if the View did not have
     * a previously-tracked listener.
     */
    private fun <T> trackListener(view: View, listener: T?, listenerResourceId: Int): T? {
        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
            val oldValue = view.getTag(listenerResourceId) as T
            view.setTag(listenerResourceId, listener)
            return oldValue
        } else {
            synchronized(globalListeners) {
                var listeners: WeakHashMap<View, WeakReference<*>>? = globalListeners.get(listenerResourceId)
                if (listeners == null) {
                    listeners = WeakHashMap()
                    globalListeners.put(listenerResourceId, listeners)
                }
                val oldValue: WeakReference<T>?
                oldValue = if (listener == null) {
                    listeners.remove(view) as WeakReference<T>
                } else {
                    listeners.put(view, WeakReference(listener)) as WeakReference<T>
                }
                return oldValue.get()
            }
        }
    }
}
