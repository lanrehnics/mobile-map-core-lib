package com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model

import com.google.gson.annotations.SerializedName

data class AssetClasses (

//		@SerializedName("name") val name : String,
		@SerializedName("translate_name") val translate_name : String,
		@SerializedName("unit") val unit : String,
		@SerializedName("type") val type : String,
		@SerializedName("translate_type") val translate_type : String,
		@SerializedName("size") val size : List<Size>
){
	override fun toString(): String = type
}