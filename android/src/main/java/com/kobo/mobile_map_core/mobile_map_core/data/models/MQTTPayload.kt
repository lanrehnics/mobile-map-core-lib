package com.kobo.mobile_map_core.mobile_map_core.data.models

import com.google.gson.annotations.SerializedName

data class MQTTPayload(


        @SerializedName("regNumber") val regNumber: String?,
        @SerializedName("tripId") val tripId: String?,
        @SerializedName("tripReadId") val tripReadId: String?,
        @SerializedName("bearing") val bearing: Float?,
        @SerializedName("lat") val lat: Double?,
        @SerializedName("lng") val lng: Double?,
        @SerializedName("accuracy") val accuracy: Float?,
        @SerializedName("speed") val speed: Float?,
        @SerializedName("distanceTraveled") val distanceTraveled: Double?,
        @SerializedName("durationRemaining") val durationRemaining: Double?,
        @SerializedName("distanceRemaining") val distanceRemaining: Double?
//        @SerializedName("distance") val distance: Float
)