package com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Location(

        @SerializedName("type") val type: String,
        @SerializedName("coordinates") val coordinates: List<Double>,
        @SerializedName("address") val address: String,
        @SerializedName("geohash") val geohash: String,
        @SerializedName("lat") val lat: Double,
        @SerializedName("lng") val lng: Double,
        @SerializedName("bearing") val bearing: Double,
        @SerializedName("speed") val speed: Double,
        @SerializedName("timestamp") val timestamp: String,
        @SerializedName("_id") val _id: String
) : Serializable