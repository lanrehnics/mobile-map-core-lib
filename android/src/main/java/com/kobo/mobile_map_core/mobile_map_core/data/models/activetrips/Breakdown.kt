package com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Breakdown (

		@SerializedName("location") val location : Location,
		@SerializedName("address") val address : String,
		@SerializedName("count") val count : Int
): Serializable