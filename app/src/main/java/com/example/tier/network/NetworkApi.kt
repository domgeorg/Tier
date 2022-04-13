package com.example.tier.network

import com.example.tier.data.remote.response.VehiclesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkApi {

    @GET("{id}")
    suspend fun getVehicles(
        @Path("id") id: String,
        @Query("apiKey") apiKey: String
    ): VehiclesResponse
}
