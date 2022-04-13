package com.example.tier.common.extensions

import androidx.fragment.app.Fragment
import com.example.tier.common.permission.Permission

internal fun Permission.requiresRationale(fragment: Fragment) =
    permissions.any { fragment.shouldShowRequestPermissionRationale(it) }

internal fun Permission.isGranted(fragment: Fragment): Boolean =
    permissions.all { fragment.hasPermission(it) }

internal fun Permission.getAndroidPermissionList(): Array<String> =
    permissions.toList().toTypedArray()
