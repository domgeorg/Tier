package com.example.tier.common.permission

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION

sealed class Permission(vararg val permissions: String) {

    object Location : Permission(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)

    companion object {
        fun from(permission: String) = when (permission) {
            ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION -> Location
            else -> throw IllegalArgumentException("Unknown permission: $permission")
        }
    }
}
