package com.example.tier.ui.fragment.map

import android.Manifest
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tier.common.crashytics.Crashlytics
import com.example.tier.common.extensions.formatDistance
import com.example.tier.common.extensions.getDistance
import com.example.tier.common.location.LocationDelegate
import com.example.tier.common.location.SettingsResult
import com.example.tier.data.remote.RemoteDataSource
import com.example.tier.model.ScreenState
import com.example.tier.model.cluster.ClusterItemMapper
import com.example.tier.model.cluster.Vehicle
import com.example.tier.model.cluster.VehicleClusterItem
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val crashlytics: Crashlytics,
    private val locationDelegate: LocationDelegate,
    private val remoteDataSource: RemoteDataSource,
    private val mapper: ClusterItemMapper
) : ViewModel() {

    private val locationRequest by lazy {
        LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(5000)
            .setFastestInterval(2500)
    }

    private var locationUpdatesJob: Job? = null

    private var currentLocation: LatLng? = null

    private val mutableLocationUpdates = MutableLiveData<Location>()
    val locationUpdates: LiveData<Location> = mutableLocationUpdates

    private val mutableResolveSettingsEvent = MutableLiveData<SettingsResult.Resolvable>()
    val resolveSettingsEvent: LiveData<SettingsResult.Resolvable> = mutableResolveSettingsEvent

    private val mutableClusterItems =
        MutableLiveData<ScreenState<List<VehicleClusterItem>>>()
    val clusterItems: LiveData<ScreenState<List<VehicleClusterItem>>> = mutableClusterItems

    private val mutableVehicleDistancePair = MutableLiveData<Pair<Vehicle, String>>()
    val vehicleDistancePair: LiveData<Pair<Vehicle, String>> = mutableVehicleDistancePair

    private val mutableClosestVehicleDistancePair = MutableLiveData<Pair<Vehicle, String>>()
    val vehicleClosestDistancePair: LiveData<Pair<Vehicle, String>> =
        mutableClosestVehicleDistancePair

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun startLocationUpdatesAfterCheck() {
        viewModelScope.launch {
            when (val settingsResult = locationDelegate.checkLocationSettings(locationRequest)) {
                SettingsResult.Satisfied -> {
                    locationDelegate.getLastLocation()?.run(mutableLocationUpdates::postValue)
                    startLocationUpdates()
                }
                is SettingsResult.Resolvable -> mutableResolveSettingsEvent.postValue(settingsResult)
                is SettingsResult.NotResolvable -> crashlytics.log(settingsResult.exception)
            }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun startLocationUpdates() {
        locationUpdatesJob?.cancel()
        locationUpdatesJob = viewModelScope.launch {
            try {
                locationDelegate.getLocationUpdate(locationRequest).also {
                    currentLocation = LatLng(it.latitude, it.longitude)
                    mutableLocationUpdates.postValue(it)
                }
            } catch (e: CancellationException) {
                crashlytics.log("MapViewModel", "Location updates cancelled: ${e.message}")
            }
        }
    }

    fun stopLocationUpdates() {
        locationUpdatesJob?.cancel()
        locationUpdatesJob = null
    }

    fun getClusterItems() {
        mutableClusterItems.postValue(ScreenState.Loading)
        viewModelScope.launch {
            remoteDataSource.getVehicles().fold(
                onSuccess = {
                    val clusterItems = mapper.map(it)
                    mutableClusterItems.postValue(ScreenState.Success(clusterItems))
                },
                onFailure = {
                    mutableClusterItems.postValue(ScreenState.Error(it))
                }
            )
        }
    }

    fun onClusterItem(item: VehicleClusterItem) {
        currentLocation?.let {
            val distance = item.vehicle.latLng.getDistance(it)
            mutableVehicleDistancePair.postValue(Pair(item.vehicle, distance))
        }
    }

    fun findClosestVehicle(clusters: List<VehicleClusterItem>) {
        viewModelScope.launch(Dispatchers.IO) {
            currentLocation?.let { latLng ->
                var distance = 0.0
                clusters.minByOrNull {
                    distance = SphericalUtil.computeDistanceBetween(latLng, it.vehicle.latLng)
                    distance
                }?.also {
                    mutableClosestVehicleDistancePair.postValue(
                        Pair(it.vehicle, distance.formatDistance())
                    )
                }
            }
        }
    }
}
