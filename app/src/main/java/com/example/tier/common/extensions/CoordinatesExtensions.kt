package com.example.tier.common.extensions

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

fun LatLng.getDistance(latLng: LatLng): String {
    val distance = SphericalUtil.computeDistanceBetween(latLng, this)
    return distance.formatDistance()
}
