package com.example.tier.common.location

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationAvailability
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationDelegate @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val locationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val settings: SettingsClient by lazy { LocationServices.getSettingsClient(context) }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    suspend fun getLastLocation(): Location? = locationProvider.lastLocation.await()

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    suspend fun getLocationUpdate(locationRequest: LocationRequest): Location =
        suspendCancellableCoroutine { cont ->
            lateinit var callback: ClearLocationCallback
            callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    cont.resume(result.lastLocation)
                    locationProvider.removeLocationUpdates(callback)
                    callback.clear()
                }
            }.let(::ClearLocationCallback)

            locationProvider.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            ).apply {
                addOnCanceledListener {
                    callback.clear()
                    cont.resumeWithException(Exception(CANCEL_MESSAGE))
                }
                addOnFailureListener {
                    callback.clear()
                    cont.resumeWithException(it)
                }
            }
        }

    suspend fun checkLocationSettings(locationSettingsRequest: LocationSettingsRequest): SettingsResult =
        suspendCancellableCoroutine { cont ->
            settings.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener { cont.resume(SettingsResult.Satisfied) }
                .addOnCanceledListener {
                    cont.resumeWithException(
                        Exception(CANCEL_MESSAGE)
                    )
                }
                .addOnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        SettingsResult.Resolvable(exception)
                    } else {
                        SettingsResult.NotResolvable(exception)
                    }.run(cont::resume)
                }
        }

    suspend fun checkLocationSettings(locationRequest: LocationRequest): SettingsResult =
        checkLocationSettings(
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        )

    companion object {
        private const val CANCEL_MESSAGE = "Task was cancelled"
    }
}

private class ClearLocationCallback(callback: LocationCallback) : LocationCallback() {

    private var callback: LocationCallback? = callback

    override fun onLocationAvailability(locationAvailability: LocationAvailability) {
        callback?.onLocationAvailability(locationAvailability)
    }

    override fun onLocationResult(locationResult: LocationResult) {
        callback?.onLocationResult(locationResult)
    }

    fun clear() {
        callback = null
    }
}
