package com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Congestions (

		@SerializedName("startLocation") val startLocation : StartLocation,
		@SerializedName("endLocation") val endLocation : EndLocation,
		@SerializedName("delayInMinutes") val delayInMinutes : Int,
		@SerializedName("count") val count : Int
): Serializable