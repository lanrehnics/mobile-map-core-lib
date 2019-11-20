package com.kobo360.models.polyline

import com.google.gson.annotations.SerializedName


data class Steps (

        @SerializedName("distance") val distance : Distance,
        @SerializedName("duration") val duration : Duration,
        @SerializedName("end_location") val end_location : EndLocation,
        @SerializedName("html_instructions") val html_instructions : String,
        @SerializedName("polyline") val polyline : Polyline,
        @SerializedName("start_location") val start_location : StartLocation,
        @SerializedName("travel_mode") val travel_mode : String
)