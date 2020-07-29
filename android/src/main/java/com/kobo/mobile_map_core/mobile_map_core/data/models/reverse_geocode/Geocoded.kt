package com.kobo.mobile_map_core.mobile_map_core.data.models.reverse_geocode

import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Location


data class Geocoded (

		@SerializedName("_id") val _id : String,
		@SerializedName("location") val location : Location,
		@SerializedName("createdAt") val createdAt : String,
		@SerializedName("updatedAt") val updatedAt : String,
		@SerializedName("__v") val __v : Int
)