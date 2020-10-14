package com.kobo.mobile_map_core.mobile_map_core.data.models.reverse_geocode

import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.reverse_geocode.Geocoded


data class Data (

	@SerializedName("geocoded") val geocoded : Geocoded
)