package com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview

import com.google.gson.annotations.SerializedName

data class Products(

        @SerializedName("name") val name: String?,
        @SerializedName("id") val id: Int?,
        @SerializedName("unit") val unit: String?
)