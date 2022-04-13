package com.example.tier.common.extensions

import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterItem

fun GoogleMap.zoom(location: Location, zoom: Zoom) {
    this.zoom(LatLng(location.latitude, location.longitude), zoom)
}

fun GoogleMap.zoom(latLng: LatLng, zoom: Zoom) =
    animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom.level))

fun GoogleMap.zoom(list: List<ClusterItem>, padding: Int) {
    val bounds = LatLngBounds.builder().also {
        list.forEach { item ->
            it.include(item.position)
        }
    }.build()
    animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
}

enum class Zoom(val level: Float) {
    Large(14f),
    Medium(17f),
    Small(21f)
}
