package com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview

import com.google.gson.annotations.SerializedName

data class OverviewResponse (



		@SerializedName("success") val success : Boolean?,
		@SerializedName("message") val message : String?,
		@SerializedName("data") val data : OverviewData?
)