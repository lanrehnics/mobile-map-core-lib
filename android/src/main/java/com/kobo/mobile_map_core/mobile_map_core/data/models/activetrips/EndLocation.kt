package com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EndLocation (

	@SerializedName("type") val type : String,
	@SerializedName("coordinates") val coordinates : List<Double>,
	@SerializedName("address") val address : String
): Serializable