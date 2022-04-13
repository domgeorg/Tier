package com.example.tier.data.remote.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VehiclesResponse(
    val data: List<Vehicle?>?
)

@JsonClass(generateAdapter = true)
data class Vehicle(
    val type: String?,
    val id: String?,
    val attributes: Attributes?
)

@JsonClass(generateAdapter = true)
data class Attributes(
    val batteryLevel: Int?,
    val lat: Double?,
    val lng: Double?,
    val maxSpeed: Long?,
    val vehicleType: String?,
    val hasHelmetBox: Boolean?
)
