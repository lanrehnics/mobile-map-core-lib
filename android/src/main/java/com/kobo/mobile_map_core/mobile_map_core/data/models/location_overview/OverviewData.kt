package com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OverviewData (
	@SerializedName("overview") val overview : Overview?
):Serializable