package com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips

import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.FlaggedBy
import java.io.Serializable

data class FlagHistory (

        @SerializedName("status") val status : Boolean,
        @SerializedName("flaggedReason") val flaggedReason : String,
        @SerializedName("flaggedBy") val flaggedBy : FlaggedBy,
        @SerializedName("dateTime") val dateTime : String,
        @SerializedName("type") val type : String,
        @SerializedName("coordinates") val coordinates : List<Double>,
        @SerializedName("geohash") val geohash : String,
        @SerializedName("address") val address : String
): Serializable