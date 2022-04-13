package com.example.tier.ui.fragment.map

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.tier.common.crashytics.Crashlytics
import com.example.tier.common.extensions.getDistance
import com.example.tier.common.location.LocationDelegate
import com.example.tier.common.location.SettingsResult
import com.example.tier.data.remote.RemoteDataSource
import com.example.tier.data.remote.response.Attributes
import com.example.tier.data.remote.response.Vehicle
import com.example.tier.data.remote.response.VehiclesResponse
import com.example.tier.model.ScreenState
import com.example.tier.model.cluster.ClusterItemMapper
import com.example.tier.model.cluster.VehicleClusterItem
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import com.example.tier.model.cluster.Vehicle as VehicleUI

@ExperimentalCoroutinesApi
class MapViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockCrashlytics: Crashlytics

    @Mock
    private lateinit var mockLocationDelegate: LocationDelegate

    @Mock
    private lateinit var mockRemoteDataSource: RemoteDataSource

    @Mock
    private lateinit var mockMapper: ClusterItemMapper

    @Mock
    private lateinit var mockLocation: Location

    @Mock
    private lateinit var updatedMockLocation: Location

    @Mock
    private lateinit var mockResolvableApiException: ResolvableApiException

    @Mock
    private lateinit var mockException: Exception

    private lateinit var viewModel: MapViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = MapViewModel(
            crashlytics = mockCrashlytics,
            locationDelegate = mockLocationDelegate,
            remoteDataSource = mockRemoteDataSource,
            mapper = mockMapper
        )
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    //region startLocationUpdatesAfterCheck
    @Test
    fun `given SettingsResult Satisfied when startLocationUpdatesAfterCheck then should return location`() =
        runBlocking {
            `when`(mockLocationDelegate.checkLocationSettings(any<LocationRequest>())).thenReturn(
                SettingsResult.Satisfied
            )
            `when`(mockLocationDelegate.getLastLocation()).thenReturn(mockLocation)
            `when`(mockLocationDelegate.getLocationUpdate(any())).thenReturn(updatedMockLocation)

            val locations = mutableListOf<Location>()
            viewModel.locationUpdates.observeForever(locations::add)

            viewModel.startLocationUpdatesAfterCheck()

            assertEquals(listOf(mockLocation, updatedMockLocation), locations)
        }

    @Test
    fun `given SettingsResult Resolvable when startLocationUpdatesAfterCheck then should return ResolveSettingsEvent`() =
        runBlocking {
            val settingsResult = SettingsResult.Resolvable(mockResolvableApiException)
            `when`(mockLocationDelegate.checkLocationSettings(any<LocationRequest>())).thenReturn(
                settingsResult
            )

            viewModel.startLocationUpdatesAfterCheck()

            viewModel.resolveSettingsEvent.observeForever {
                assertEquals(settingsResult, it)
            }
        }

    @Test
    fun `given SettingsResult NotResolvable when startLocationUpdatesAfterCheck then should call crashlytics log`() =
        runBlocking {
            `when`(mockLocationDelegate.checkLocationSettings(any<LocationRequest>())).thenReturn(
                SettingsResult.NotResolvable(mockException)
            )

            viewModel.startLocationUpdatesAfterCheck()

            verify(mockCrashlytics).log(mockException)
        }
    //endregion

    //region startLocationUpdates
    @Test
    fun `given location update when startLocationUpdates then should return location`() =
        runBlocking {
            `when`(mockLocationDelegate.getLocationUpdate(any())).thenReturn(mockLocation)

            viewModel.startLocationUpdates()

            viewModel.locationUpdates.observeForever {
                assertEquals(mockLocation, it)
            }
        }

    @Test
    fun `given CancellationException when startLocationUpdates then should call crashlytics log`() =
        runBlocking {
            val exception = CancellationException("Test exception")
            `when`(mockLocationDelegate.getLocationUpdate(any())).thenThrow(exception)

            viewModel.startLocationUpdates()

            verify(mockCrashlytics).log(any(), any())
        }
    //endregion

    //region getClusterItems
    @Test
    fun `given success when getClusterItems then should return ScreenState Success`() =
        runBlocking {
            val loading = ScreenState.Loading
            val successScreenState = ScreenState.Success(dummyClusterItems)
            `when`(mockRemoteDataSource.getVehicles()).thenReturn(
                Result.success(dummyVehiclesResponse)
            )
            `when`(mockMapper.map(any())).thenReturn(dummyClusterItems)

            val screenStates = mutableListOf<ScreenState<List<VehicleClusterItem>>>()
            viewModel.clusterItems.observeForever(screenStates::add)

            viewModel.getClusterItems()

            assertEquals(listOf(loading, successScreenState), screenStates)
        }

    @Test
    fun `given failure when getClusterItems then should return ScreenState Error`() =
        runBlocking {
            val exception = Exception("Test Exception")
            val loading = ScreenState.Loading
            val errorScreenState = ScreenState.Error(exception)
            `when`(mockRemoteDataSource.getVehicles()).thenReturn(Result.failure(exception))

            val screenStates = mutableListOf<ScreenState<List<VehicleClusterItem>>>()
            viewModel.clusterItems.observeForever(screenStates::add)

            viewModel.getClusterItems()

            assertEquals(listOf(loading, errorScreenState), screenStates)
        }
    //endregion

    //region onClusterItem
    @Test
    fun `given onClusterItem method call then should return the Vehicle and the distance form the current user's location`() {
        val clusterItem = dummyClusterItems[0]
        val distance = dummyCurrentLatLng.getDistance(clusterItem.vehicle.latLng)
        val currentLocation = viewModel.javaClass.getDeclaredField("currentLocation")
        currentLocation.isAccessible = true
        currentLocation.set(viewModel, dummyCurrentLatLng)

        viewModel.onClusterItem(clusterItem)

        viewModel.vehicleDistancePair.observeForever {
            assertEquals(Pair(clusterItem.vehicle, distance), it)
        }
    }
    //endregion

    //region findClosestVehicle
    @Test
    fun `given findClosestVehicle method call then should return the Vehicle with the closest distance form the current user's location`() {
        val clusterItem = dummyClusterItems[0]
        val distance = dummyCurrentLatLng.getDistance(clusterItem.vehicle.latLng)
        val currentLocation = viewModel.javaClass.getDeclaredField("currentLocation")
        currentLocation.isAccessible = true
        currentLocation.set(viewModel, dummyCurrentLatLng)

        viewModel.findClosestVehicle(dummyClusterItems)

        viewModel.vehicleDistancePair.observeForever {
            assertEquals(Pair(clusterItem.vehicle, distance), it)
        }
    }
    //endregion

    companion object {
        private val dummyVehicles = mutableListOf<Vehicle>().apply {
            add(
                Vehicle(
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
            add(
                Vehicle(
                    type = "vehicle",
                    id = "ade7e0ec",
                    attributes = Attributes(
                        batteryLevel = 60,
                        lat = 52.523796,
                        lng = 13.410436,
                        maxSpeed = 20,
                        vehicleType = "escooter",
                        hasHelmetBox = false
                    )
                )
            )
        }

        private val dummyVehiclesResponse = VehiclesResponse(data = dummyVehicles)

        private val dummyClusterItems = mutableListOf<VehicleClusterItem>().apply {
            add(
                VehicleClusterItem(
                    VehicleUI(
                        latLng = LatLng(52.515093, 13.351325),
                        type = "escooter",
                        batteryLevel = "73",
                        maxSpeed = "20",
                        hasHelmetBox = false
                    )
                )
            )
            add(
                VehicleClusterItem(
                    VehicleUI(
                        latLng = LatLng(52.523796, 13.410436),
                        type = "escooter",
                        batteryLevel = "60",
                        maxSpeed = "20",
                        hasHelmetBox = false
                    )
                )
            )
        }

        private val dummyCurrentLatLng = LatLng(52.523716, 13.410416)
    }

    /**
     * Returns Mockito.any() as nullable type to avoid java.lang.IllegalStateException when
     * null is returned.
     */
    private fun <T> any(): T = Mockito.any<T>()
}
