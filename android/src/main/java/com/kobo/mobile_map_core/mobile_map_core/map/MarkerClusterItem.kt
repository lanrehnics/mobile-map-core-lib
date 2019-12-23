package com.kobo.mobile_map_core.mobile_map_core.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.kobo.mobile_map_core.mobile_map_core.models.TruckModelDataParser

class MarkerClusterItem(
        val truckModelDataParser: TruckModelDataParser
) : ClusterItem {


    override fun getSnippet(): String {
        return truckModelDataParser.d.reg_number
    }

    override fun getTitle(): String {
        // be very careful when changing this, the title is use to filter the markerManager in clusterManager
        return truckModelDataParser.d.reg_number
    }

    override fun getPosition(): LatLng {
        return LatLng(
                truckModelDataParser.l.latitude,
                truckModelDataParser.l.longitude
        )
    }
}