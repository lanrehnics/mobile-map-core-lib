package com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Driver(
        @SerializedName("_id") val _id : String,
        @SerializedName("id") val id: Int,
        @SerializedName("firstName") val firstName: String,
        @SerializedName("lastName") val lastName: String,
        @SerializedName("mobile") val mobile: String,
        @SerializedName("image") val image: String,
        @SerializedName("rating") val rating: Int,
        @SerializedName("level") val level: Int
): Serializable