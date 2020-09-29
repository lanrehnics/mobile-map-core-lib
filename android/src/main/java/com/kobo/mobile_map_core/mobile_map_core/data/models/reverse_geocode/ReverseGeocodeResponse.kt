package com.kobo.mobile_map_core.mobile_map_core.data.models.reverse_geocode

import com.google.gson.annotations.SerializedName

data class ReverseGeocodeResponse (

		@SerializedName("success") val success : Boolean,
		@SerializedName("message") val message : String,
		@SerializedName("data") val data : Data
)