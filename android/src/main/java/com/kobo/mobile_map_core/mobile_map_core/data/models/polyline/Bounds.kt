package com.kobo.mobile_map_core.mobile_map_core.data.models.polyline

import com.google.gson.annotations.SerializedName
import com.kobo360.models.polyline.Northeast
import com.kobo360.models.polyline.Southwest


data class Bounds (

		@SerializedName("northeast") val northeast : Northeast,
		@SerializedName("southwest") val southwest : Southwest
)