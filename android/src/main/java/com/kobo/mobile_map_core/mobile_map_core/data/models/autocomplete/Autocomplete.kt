package com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete

import Terms
import com.google.android.libraries.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class Autocomplete (
		@SerializedName("description") var description : String?,
		@SerializedName("placeId") val placeId : String?,
		@SerializedName("terms") val terms : List<Terms>?,
		@SerializedName("latLng") var latLng : LatLng?
)