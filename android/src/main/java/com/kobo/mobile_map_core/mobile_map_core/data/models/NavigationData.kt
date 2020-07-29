package com.kobo.mobile_map_core.mobile_map_core.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NavigationData(
        @SerializedName("regNumber") val regNumber: String?,
        @SerializedName("tripId") val tripId: String?,
        @SerializedName("tripReadId") val tripReadId: String?,
        @SerializedName("destination") val destination: List<Double>?
) : Serializable