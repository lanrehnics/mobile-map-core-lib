package com.kobo.mobile_map_core.mobile_map_core.data.models

import com.google.android.libraries.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import com.kobo.mobile_map_core.mobile_map_core.data.models.orders.Orders

class OrderClusterItem(
        val order: Orders?
) : ClusterItem {

    override fun getSnippet(): String? {
        return order?.pickupLocation?.address
    }

    override fun getTitle(): String {
        return order?.id!!
    }

    override fun getPosition(): LatLng {
        return order?.pickupLocation?.coordinates?.get(0)?.let {
            LatLng(
                    order.pickupLocation.coordinates[1],
                    it
            )
        }!!
    }
}