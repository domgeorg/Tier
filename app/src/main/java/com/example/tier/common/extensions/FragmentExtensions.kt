package com.example.tier.common.extensions

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.tier.R
import com.example.tier.common.permission.Permission
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Fragment.permissionRequest(
    permission: Permission,
    message: String?,
    onGrand: (Boolean) -> Unit
) {
    val requestPermissions = permission.getAndroidPermissionList()
    when {
        permission.isGranted(this) -> {
            onGrand(true)
        }
        permission.requiresRationale(this) -> showPermissionDialog(
            requestPermissions,
            message,
            onGrand
        )
        else -> requestForPermissions(requestPermissions, onGrand)
    }
}

internal fun Fragment.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        this.requireContext(),
        permission
    ) == PackageManager.PERMISSION_GRANTED

private fun Fragment.requestForPermissions(
    permissions: Array<String>,
    onGrand: (Boolean) -> Unit
) {
    registerForActivityResult(RequestMultiplePermissions()) { results ->
        onGrand(results.all { it.value })
    }.also { it.launch(permissions) }
}

private fun Fragment.showPermissionDialog(
    permissions: Array<String>,
    message: String?,
    onGrand: (Boolean) -> Unit
) {
    if (!isAdded) {
        return
    }
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.dialog_permission_title)
        .setMessage(message ?: getString(R.string.dialog_permission_default_message))
        .setCancelable(false)
        .setPositiveButton(R.string.dialog_permission_button_positive) { _, _ ->
            requestForPermissions(permissions, onGrand)
        }
}
