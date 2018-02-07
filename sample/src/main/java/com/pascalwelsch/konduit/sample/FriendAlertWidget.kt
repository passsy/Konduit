package com.pascalwelsch.konduit.sample

import android.support.v7.app.AlertDialog
import com.pascalwelsch.konduit.widget.Widget
import com.pascalwelsch.konduit.widget.WidgetListBuilder

/**
 * DSL access for the [FriendAlertWidget]
 */
fun WidgetListBuilder.friendAlert(init: FriendAlertWidget.() -> Unit): FriendAlertWidget = add(FriendAlertWidget(), init)

/**
 * Custom widget which represents a simple dialog
 *
 * Note that it doesn't implement all properties of [AlertDialog], only those which are required
 */
open class FriendAlertWidget : Widget() {

    open var message: String? = null
        set(value) {
            checkWritability()
            field = value
        }

    open var onDismiss: (() -> Unit)? = null
        set(value) {
            checkWritability()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FriendAlertWidget) return false
        if (!super.equals(other)) return false

        if (message != other.message) return false
        if (onDismiss != other.onDismiss) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + (onDismiss?.hashCode() ?: 0)
        return result
    }
}