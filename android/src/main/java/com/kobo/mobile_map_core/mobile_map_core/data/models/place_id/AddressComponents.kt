package com.kobo.mobile_map_core.mobile_map_core.data.models.place_id

import com.google.gson.annotations.SerializedName

data class AddressComponents (

		@SerializedName("long_name") val long_name : String,
		@SerializedName("short_name") val short_name : String,
		@SerializedName("types") val types : List<String>
)