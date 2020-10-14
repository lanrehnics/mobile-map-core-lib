package com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks

import com.google.gson.annotations.SerializedName

data class AvailableTruckResponse(

        @SerializedName("success") val success: Boolean,
        @SerializedName("message") val message: String,
        @SerializedName("data") val data: AvailableTruckData
)