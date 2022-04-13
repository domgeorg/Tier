package com.example.tier.common.extensions

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(message: Int, length: Int = Snackbar.LENGTH_INDEFINITE) {
    Snackbar.make(this, message, length).show()
}

fun View.showSnackbar(
    message: Int,
    length: Int = Snackbar.LENGTH_INDEFINITE,
    actionText: Int,
    action: (() -> Unit)
) {
    Snackbar.make(this, message, length)
        .setAction(actionText) {
            action.invoke()
        }.show()
}
