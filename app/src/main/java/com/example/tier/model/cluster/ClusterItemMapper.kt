package com.example.tier.model.cluster

import com.example.tier.data.remote.response.VehiclesResponse
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class ClusterItemMapper @Inject constructor() {
    fun map(response: VehiclesResponse): List<VehicleClusterItem> =
        response.data?.map { vehicle ->
            vehicle?.attributes.let { attr ->
                VehicleClusterItem(
                    Vehicle(
                        latLng = LatLng(
                            attr?.lat ?: 0.0,
                            attr?.lng ?: 0.0
                        ),
                        type = attr?.vehicleType ?: "",
                        batteryLevel = (attr?.batteryLevel ?: 0).toString(),
                        maxSpeed = (attr?.maxSpeed ?: 0.0).toString(),
                        hasHelmetBox = attr?.hasHelmetBox ?: false
                    )
                )
            }
        } ?: emptyList()
}
