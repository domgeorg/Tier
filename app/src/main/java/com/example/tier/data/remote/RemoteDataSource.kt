package com.example.tier.data.remote

import com.example.tier.BuildConfig
import com.example.tier.data.remote.response.VehiclesResponse
import com.example.tier.network.NetworkApi
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val networkApi: NetworkApi) {

    suspend fun getVehicles(): Result<VehiclesResponse> = try {
        Result.success(
            networkApi.getVehicles(
                id = BuildConfig.SERVICE_ID,
                apiKey = BuildConfig.API_KEY
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
}
