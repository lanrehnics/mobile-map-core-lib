package com.kobo.mobile_map_core.mobile_map_core.data.models.place_id

import com.google.gson.annotations.SerializedName

data class Place (

		@SerializedName("addressComponents") val addressComponents : List<AddressComponents>,
		@SerializedName("formattedAddress") val formattedAddress : String,
		@SerializedName("geometry") val geometry : Geometry,
		@SerializedName("placeId") val placeId : String
)