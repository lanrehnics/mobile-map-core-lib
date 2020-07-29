package com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Diversions (

		@SerializedName("reason") val reason : String,
		@SerializedName("dateTime") val dateTime : String,
		@SerializedName("type") val type : String,
		@SerializedName("coordinates") val coordinates : List<Double>,
		@SerializedName("geohash") val geohash : String,
		@SerializedName("address") val address : String
): Serializable