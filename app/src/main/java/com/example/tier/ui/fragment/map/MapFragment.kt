package com.example.tier.ui.fragment.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.location.Location
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tier.R
import com.example.tier.common.cluster.ClusterRenderer
import com.example.tier.common.extensions.mapBound
import com.example.tier.common.extensions.permissionRequest
import com.example.tier.common.extensions.showSnackbar
import com.example.tier.common.extensions.zoom
import com.example.tier.common.extensions.Zoom
import com.example.tier.common.extensions.bitmapDescriptorFrom
import com.example.tier.common.location.SettingsResult
import com.example.tier.common.permission.Permission
import com.example.tier.common.play_services.PlayServicesAvailabilityChecker
import com.example.tier.common.view_binding.viewBinding
import com.example.tier.databinding.FragmentMapBinding
import com.example.tier.model.ScreenState
import com.example.tier.model.cluster.Vehicle
import com.example.tier.model.cluster.VehicleClusterItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) {

    @Inject
    lateinit var googleAvailabilityChecker: PlayServicesAvailabilityChecker

    private val binding by viewBinding(FragmentMapBinding::bind)

    private val viewModel: MapViewModel by viewModels()

    private var map: GoogleMap? = null

    private var currentLocationMarker: Marker? = null

    private lateinit var clusterManager: ClusterManager<VehicleClusterItem>

    private lateinit var clusterRenderer: ClusterRenderer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        googleAvailabilityChecker.isGooglePlayServicesAvailable(
            available = {
                askPermission()
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync {
                    map = it
                    initClusters()
                    bindObservers()
                    viewModel.getClusterItems()
                }
            },
            unavailable = {
                binding.root.showSnackbar(R.string.error_google_play_services)
            }
        )
    }

    override fun onDestroy() {
        viewModel.stopLocationUpdates()
        map?.clear()
        currentLocationMarker = null
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            viewModel.startLocationUpdates()
            return
        }
        binding.root.showSnackbar(R.string.error_permission)
    }

    @SuppressLint("MissingPermission")
    private fun askPermission() {
        permissionRequest(
            Permission.Location,
            getString(R.string.location_permission_message)
        ) { isGranted ->
            if (isGranted) {
                viewModel.startLocationUpdatesAfterCheck()
                return@permissionRequest
            }
            binding.root.showSnackbar(R.string.error_permission)
        }
    }

    private fun bindObservers() {
        viewModel.locationUpdates.observe(viewLifecycleOwner, this::onLocationUpdate)
        viewModel.resolveSettingsEvent.observe(viewLifecycleOwner, this::resolveSettings)
        viewModel.clusterItems.observe(viewLifecycleOwner, this::onClusters)
        viewModel.vehicleDistancePair.observe(viewLifecycleOwner, this::navigateToVehicleDetails)
        viewModel.vehicleClosestDistancePair.observe(
            viewLifecycleOwner,
            this::showClosestVehicleDetails
        )
    }

    private fun initClusters() {
        clusterManager = ClusterManager(activity, map)
        clusterRenderer = ClusterRenderer(context, map, clusterManager)

        with(clusterManager) {
            renderer = clusterRenderer
            algorithm = NonHierarchicalDistanceBasedAlgorithm()
            map?.setOnMarkerClickListener(this)
            map?.setOnCameraIdleListener(this)
            setOnClusterItemClickListener { clusterItem ->
                viewModel.onClusterItem(clusterItem)
                true
            }
            setOnClusterClickListener {
                val clusterItems = it.items.toList()
                val padding = context?.mapBound() ?: 0
                map?.zoom(clusterItems, padding)
                true
            }
        }
    }

    private fun resolveSettings(result: SettingsResult.Resolvable) {
        startIntentSenderForResult(
            result.exception.resolution.intentSender, SETTINGS_REQUEST_CODE, null, 0, 0, 0, null
        )
    }

    private fun onClusters(screenState: ScreenState<List<VehicleClusterItem>>) {
        binding.loading.visibility = View.GONE
        when (screenState) {
            is ScreenState.Loading -> binding.loading.visibility = View.VISIBLE

            is ScreenState.Error -> binding.root.showSnackbar(
                message = R.string.error_load_scooters,
                actionText = R.string.try_again
            ) { viewModel.getClusterItems() }

            is ScreenState.Success -> {
                clusterManager.apply {
                    addItems(screenState.value)
                    cluster()
                }

                binding.closestVehicle.setOnClickListener {
                    val clusters = clusterManager.algorithm.items as? List<VehicleClusterItem>
                    clusters?.also {
                        viewModel.findClosestVehicle(it)
                    }
                }

                binding.closestVehicle.visibility = View.VISIBLE
                binding.closestVehicle.startAnimation(
                    AnimationUtils.loadAnimation(
                        activity,
                        R.anim.ic_heart_pulse
                    )
                )
            }
        }
    }

    private fun onLocationUpdate(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        if (currentLocationMarker == null) {
            currentLocationMarker = map?.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(activity?.bitmapDescriptorFrom(R.drawable.ic_my_location))
            )
            map?.zoom(location, Zoom.Large)
            return
        }

        currentLocationMarker?.also {
            it.position = latLng
        }
    }

    private fun showClosestVehicleDetails(vehicleDistancePair: Pair<Vehicle, String>) {
        navigateToVehicleDetails(vehicleDistancePair)
        map?.zoom(vehicleDistancePair.first.latLng, Zoom.Medium)
    }

    private fun navigateToVehicleDetails(vehicleDistancePair: Pair<Vehicle, String>) {
        vehicleDistancePair.also { (vehicle, distance) ->
            findNavController().navigate(
                MapFragmentDirections.actionToVehicleFragment(vehicle, distance)
            )
        }
    }

    companion object {
        private const val SETTINGS_REQUEST_CODE = 893
    }
}
