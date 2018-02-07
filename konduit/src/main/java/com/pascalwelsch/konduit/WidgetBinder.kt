package com.pascalwelsch.konduit

import android.util.Log
import com.pascalwelsch.konduit.widget.Widget

/**
 * Friendly to work with [AndroidViewRenderer]
 */
interface WidgetBinder {

    /**
     * use this renderer directly for more advanced features, like removing [ViewBindingAdapter]s
     */
    val renderer: AndroidViewRenderer

    /**
     * Create a binding for a single key which can have a dynamic View.
     */
    fun <W : Widget> bind(key: Any, onChange: ((W) -> Unit)? = null, onAdded: ((W) -> Unit)? = null,
            onRemoved: ((W) -> Unit)? = null) {
        bind(key, object : ViewBinding<W> {
            override fun onChanged(widget: W) {
                onChange?.invoke(widget)
            }

            override fun onAdded(widget: W) {
                onAdded?.invoke(widget)
            }

            override fun onRemoved(widget: W) {
                onRemoved?.invoke(widget)
            }
        })
    }

    /**
     * Create a binding for a single key which can have a dynamic View.
     */
    fun <W : Widget> bind(key: Any, binding: ViewBinding<W>) {
        val bindings = renderer.bindingsFor(key)

        if (bindings.count() > 0) {
            Log.v("Bind", " - $key already has ${bindings.count()} bindings")
            Log.v("Bind", " - bindings: ${bindings.joinToString("\n")}")
        }

        bindings.add(binding)
    }

    /**
     * Adds a [ViewBindingAdapter] which generates bindings for all matching [Widget]s
     */
    fun addAdapter(adapter: ViewBindingAdapter) {
        renderer.adapters.add(adapter)
    }
}