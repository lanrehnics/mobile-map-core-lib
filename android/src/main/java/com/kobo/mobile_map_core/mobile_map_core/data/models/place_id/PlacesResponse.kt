package com.kobo.mobile_map_core.mobile_map_core.data.models.place_id

import com.google.gson.annotations.SerializedName

data class PlacesResponse (
		@SerializedName("success") val success : Boolean,
		@SerializedName("message") val message : String,
		@SerializedName("data") val data : PlacesData
)