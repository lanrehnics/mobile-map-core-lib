package com.kobo360.map

import android.content.Context
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.models.OrderClusterItem
import com.kobo.mobile_map_core.mobile_map_core.data.models.TruckClusterItem
import com.kobo.mobile_map_core.mobile_map_core.ui.map.NewBaseMapActivity


class OrderMarkerClusterRenderer<T : ClusterItem> internal constructor(
        context: Context,
        private val googleMap: GoogleMap,
        private val clusterManager: ClusterManager<T>
) : DefaultClusterRenderer<T>(context, googleMap, clusterManager), GoogleMap.OnCameraMoveListener {

    private var currentZoomLevel = 0f
    private var maxZoomLevel = 7f


    override fun shouldRenderAsCluster(cluster: Cluster<T>): Boolean {


        var superWouldCluster = super.shouldRenderAsCluster(cluster)

        if (superWouldCluster) {
            superWouldCluster = currentZoomLevel < maxZoomLevel
        }

        return superWouldCluster && cluster.size > 10000
    }


    override fun onCameraMove() {
        currentZoomLevel = googleMap.cameraPosition.zoom
    }


    override fun onBeforeClusterItemRendered(item: T, markerOptions: MarkerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_customerlocation))
        markerOptions.snippet(item.snippet)
        markerOptions.title(item.title)
        markerOptions.let { super.onBeforeClusterItemRendered(item, it) }
    }
}