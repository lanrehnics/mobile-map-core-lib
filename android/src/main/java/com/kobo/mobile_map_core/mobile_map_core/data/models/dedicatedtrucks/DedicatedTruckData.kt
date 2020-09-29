package com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks

import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Breakdown
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Congestions
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.TruckData


data class DedicatedTruckData(
        @SerializedName("trucks") val truckData: TruckData
)