package com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ActivePartner(

        @SerializedName("_id") val _id: String,
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String
):Serializable