package com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks

import com.google.gson.annotations.SerializedName

data class AvailableTruckData (

	@SerializedName("trucks") val truckData : TruckData
)