package com.kobo360.models.polyline

import com.google.gson.annotations.SerializedName

data class GeocodedWaypoints (

		@SerializedName("geocoder_status") val geocoder_status : String,
		@SerializedName("place_id") val place_id : String,
		@SerializedName("types") val types : List<String>
)