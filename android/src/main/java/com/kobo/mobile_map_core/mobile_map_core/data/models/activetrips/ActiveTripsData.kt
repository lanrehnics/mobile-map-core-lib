package com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ActiveTripsData (

	@SerializedName("trips") val tripsData : TripData
): Serializable