package com.kobo.mobile_map_core.mobile_map_core.data.models

import com.google.gson.annotations.SerializedName

data class BookTruckResponse(
		@SerializedName("status") val status: Boolean,
		@SerializedName("unbookedReason") val unbookedReason: String
)