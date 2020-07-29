package com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model

import com.google.gson.annotations.SerializedName

data class AssetClassResponse (

		@SerializedName("success") val success : Boolean,
		@SerializedName("message") val message : String,
		@SerializedName("data") val data : AssetClassData
)