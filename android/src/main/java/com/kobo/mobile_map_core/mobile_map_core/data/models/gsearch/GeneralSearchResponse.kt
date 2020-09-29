package com.kobo.mobile_map_core.mobile_map_core.data.models.gsearch

import com.google.gson.annotations.SerializedName

data class GeneralSearchResponse(
		@SerializedName("success") val success: Boolean,
		@SerializedName("message") val message: String,
		@SerializedName("data") val data: GeneralSearchData
)