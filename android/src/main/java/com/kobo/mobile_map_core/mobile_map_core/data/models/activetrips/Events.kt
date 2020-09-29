package com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Events (

		@SerializedName("type") val type : String,
		@SerializedName("name") val name : String,
		@SerializedName("datetime") val datetime : String,
		@SerializedName("location") val location : Location
): Serializable