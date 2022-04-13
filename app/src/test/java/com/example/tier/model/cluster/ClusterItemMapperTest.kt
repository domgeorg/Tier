package com.example.tier.model.cluster

import com.example.tier.data.remote.response.Attributes
import com.example.tier.data.remote.response.VehiclesResponse
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Test

class ClusterItemMapperTest {

    private val clusterItemMapper = ClusterItemMapper()

    @Test
    fun `given null VehicleResponse data when map VehicleClusterItem then should return empty list`() {
        val expected = emptyList<VehicleClusterItem>()

        val actual = clusterItemMapper.map(VehiclesResponse(null))

        assertEquals(expected, actual)
    }

    @Test
    fun `given empty VehicleResponse data when map VehicleClusterItem then should return empty list`() {
        val expected = emptyList<VehicleClusterItem>()

        val actual = clusterItemMapper.map(VehiclesResponse(emptyList()))

        assertEquals(expected, actual)
    }

    @Test
    fun `given VehicleResponse data when map VehicleClusterItem then should return the correct list with VehicleClusterItems`() {
        val expected = listOf(
            VehicleClusterItem(
                Vehicle(
                    latLng = LatLng(52.515093, 13.351325),
                    type = "escooter",
                    batteryLevel = "73",
                    maxSpeed = "20",
                    hasHelmetBox = false
                )
            )
        )

        val actual = clusterItemMapper.map(
            VehiclesResponse(
                listOf(
                    com.example.tier.data.remote.response.Vehicle(
                        type = "vehicle",
                        id = "7ba4ee38",
                        attributes = Attributes(
                            batteryLevel = 73,
                            lat = 52.515093,
                            lng = 13.351325,
                            maxSpeed = 20,
                            vehicleType = "escooter",
                            hasHelmetBox = false
                        )
                    )
                )
            )
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `given VehicleResponse data with null values when map VehicleClusterItem then should return the correct list with VehicleClusterItems`() {
        val expected = listOf(
            VehicleClusterItem(
                Vehicle(
                    latLng = LatLng(52.515093, 13.351325),
                    type = "",
                    batteryLevel = "0",
                    maxSpeed = "0.0",
                    hasHelmetBox = false
                )
            )
        )

        val actual = clusterItemMapper.map(
            VehiclesResponse(
                listOf(
                    com.example.tier.data.remote.response.Vehicle(
                        type = null,
                        id = "7ba4ee38",
                        attributes = Attributes(
                            batteryLevel = null,
                            lat = 52.515093,
                            lng = 13.351325,
                            maxSpeed = null,
                            vehicleType = null,
                            hasHelmetBox = null
                        )
                    )
                )
            )
        )

        assertEquals(expected, actual)
    }
}
