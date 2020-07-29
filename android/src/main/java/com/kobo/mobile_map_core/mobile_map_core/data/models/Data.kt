package com.kobo360.models

import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.Locations
import com.kobo.mobile_map_core.mobile_map_core.data.models.MQTTPayload

data class Data (

	@SerializedName("locations") val locations : List<Locations>
)