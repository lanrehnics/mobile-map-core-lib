package com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks

import com.google.gson.annotations.SerializedName

data class LastTenLocations (

		@SerializedName("coordinates") val coordinates : List<Double>,
		@SerializedName("address") val address : String,
		@SerializedName("geohash") val geohash : String
)