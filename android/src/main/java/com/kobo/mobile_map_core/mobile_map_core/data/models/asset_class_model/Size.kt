package com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model

import com.google.gson.annotations.SerializedName

data class Size (

		@SerializedName("_id") val _id : String,
		@SerializedName("size") val size : Int
){
	override fun toString(): String = size.toString()
}