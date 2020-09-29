package com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Analytics (

		@SerializedName("toPickup") val toPickup : Int,
		@SerializedName("toDelivery") val toDelivery : Int,
		@SerializedName("atDestination") val atDestination : Int,
		@SerializedName("stopped") val stopped : Int,
		@SerializedName("diverted") val diverted : Int
): Serializable