package com.example.tier.common.cluster

import android.content.Context
import com.example.tier.R
import com.example.tier.common.extensions.bitmapDescriptorFrom
import com.example.tier.model.cluster.VehicleClusterItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class ClusterRenderer(
    val context: Context?,
    map: GoogleMap?,
    clusterManager: ClusterManager<VehicleClusterItem>
) : DefaultClusterRenderer<VehicleClusterItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(
        item: VehicleClusterItem,
        markerOptions: MarkerOptions
    ) {
        markerOptions.title(item.title)
            .position(item.position)
            .snippet(item.snippet)
            .icon(context?.bitmapDescriptorFrom(R.drawable.ic_marker))
        super.onBeforeClusterItemRendered(item, markerOptions)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<VehicleClusterItem>): Boolean {
        return cluster.size > 5
    }
}
