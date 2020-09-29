package com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks

import com.google.gson.annotations.SerializedName

data class DedicatedTruckResponse (

		@SerializedName("success") val success : Boolean,
		@SerializedName("message") val message : String,
		@SerializedName("data") val data : DedicatedTruckData
)