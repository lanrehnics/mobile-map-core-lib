package com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model
import com.google.gson.annotations.SerializedName
data class AssetClassData (
	@SerializedName("assetClasses") val assetClasses : List<AssetClasses>
)