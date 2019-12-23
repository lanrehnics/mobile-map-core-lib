package com.kobo360.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.map.MarkerClusterItem
import com.kobo.mobile_map_core.mobile_map_core.models.TripStatus


class MarkerClusterRenderer<T : ClusterItem> internal constructor(
    context: Context,
    googleMap: GoogleMap,
    val clusterManager: ClusterManager<T>
) : DefaultClusterRenderer<T>(context, googleMap, clusterManager) {


    override fun shouldRenderAsCluster(cluster: Cluster<T>): Boolean {
        return cluster.size > 1
    }

    override fun onBeforeClusterItemRendered(item: T, markerOptions: MarkerOptions?) {
        val markerClusterItem: MarkerClusterItem = item as MarkerClusterItem
        markerOptions?.icon(
            BitmapDescriptorFactory.fromResource(
                when {
                    markerClusterItem.truckModelDataParser.d.status == TripStatus.STATUS_DELIVERED -> R.drawable.kobo_truck_blue
                    markerClusterItem.truckModelDataParser.d.flagged -> R.drawable.kobo_truck_red
                    else -> R.drawable.kobo_truck_green
                }
            )
        )
        markerOptions?.rotation(markerClusterItem.truckModelDataParser.d.bearing.toFloat())
        markerOptions?.snippet(item.snippet)
        markerOptions?.title(item.title)
        super.onBeforeClusterItemRendered(item, markerOptions)
    }
}