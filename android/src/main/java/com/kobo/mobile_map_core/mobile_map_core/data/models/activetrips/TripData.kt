package com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips

import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Analytics
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Breakdown
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Congestions
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Trips
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import java.io.Serializable

data class TripData(

        @SerializedName("currentPage") val currentPage: Int?,
        @SerializedName("totalPages") val totalPages: Int?,
        @SerializedName("limit") val limit: Int?,
        @SerializedName("total") val total: Int?,
        @SerializedName("analytics") val analytics: Analytics?,
        @SerializedName("breakdown") val breakdown: List<Breakdown?>?,
        @SerializedName("congestions") val congestions: List<Congestions?>?,
//		@SerializedName("trucks") val trucks : List<Trips>?
        @SerializedName("trucks") val trucks: List<Trucks?>?
) : Serializable