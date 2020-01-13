package com.kobo.mobile_map_core.mobile_map_core.models

import com.google.gson.annotations.SerializedName
import com.kobo360.models.Data

data class KoboLocations(

        @SerializedName("data") val data: Data,
        @SerializedName("message") val message: String,
        @SerializedName("success") val success: Boolean
)