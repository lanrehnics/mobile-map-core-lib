package com.kobo.mobile_map_core.mobile_map_core.data.models

import com.google.android.libraries.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks

class TruckClusterItem(
        val truck: Trucks?
) : ClusterItem {

    override fun getSnippet(): String? {
        return truck?.lastKnownLocation?.address
    }

    override fun getTitle(): String {
        return truck?.regNumber!!
    }

    override fun getPosition(): LatLng {
        return truck?.lastKnownLocation?.coordinates?.get(0)?.let {
            LatLng(
                    truck.lastKnownLocation.coordinates[1],
                    it
            )
        }!!
    }
}