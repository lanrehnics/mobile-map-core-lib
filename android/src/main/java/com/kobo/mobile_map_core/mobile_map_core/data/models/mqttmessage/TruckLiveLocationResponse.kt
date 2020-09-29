package com.kobo.mobile_map_core.mobile_map_core.data.models.mqttmessage

import com.google.gson.annotations.SerializedName

data class TruckLiveLocationResponse (

		@SerializedName("pattern") val pattern : String,
		@SerializedName("data") val data : LiveLocationData
)