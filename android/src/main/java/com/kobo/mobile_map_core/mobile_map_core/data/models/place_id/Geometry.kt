package com.kobo.mobile_map_core.mobile_map_core.data.models.place_id

import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Location

data class Geometry (

		@SerializedName("location") val location : Location,
		@SerializedName("viewport") val viewport : Viewport
)