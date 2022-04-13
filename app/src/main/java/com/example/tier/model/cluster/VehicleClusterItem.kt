package com.example.tier.model.cluster

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import kotlinx.parcelize.Parcelize

data class VehicleClusterItem(val vehicle: Vehicle) : ClusterItem {

    override fun getPosition(): LatLng = vehicle.latLng

    override fun getTitle(): String = "${vehicle.type}: ${vehicle.batteryLevel}%"

    override fun getSnippet(): String = ""
}

@Parcelize
data class Vehicle(
    val latLng: LatLng,
    val type: String,
    val batteryLevel: String,
    val maxSpeed: String,
    val hasHelmetBox: Boolean
) : Parcelable
