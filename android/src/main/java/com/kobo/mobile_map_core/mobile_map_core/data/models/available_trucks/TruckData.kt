package com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks

import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Breakdown
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Congestions
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import java.io.Serializable

data class TruckData(
        @SerializedName("currentPage") val currentPage : Int,
        @SerializedName("totalPages") val totalPages : Int,
        @SerializedName("limit") val limit : Int,
        @SerializedName("breakdown") val breakdown : List<Breakdown>,
        @SerializedName("congestions") val congestions : List<Congestions>,
        @SerializedName("radiusInKM") val radiusInKM: Int?,
        @SerializedName("polyline") val polyline: String? = "",
        @SerializedName("type") val type: String? = "",
        @SerializedName("total") val total: Int,
        @SerializedName("congestion") val congestion: List<String>,
        @SerializedName("trucks") val trucks: List<Trucks>?
):Serializable

class TruckDataConst {

    companion object {
        val TYPE_DESTINATION: String = "destination"
        val TYPE_RADIUS: String = "radius"
    }
}