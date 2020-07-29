package com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Asset (

		@SerializedName("_id") val _id : String,
		@SerializedName("unit") val unit : String,
//		@SerializedName("name") val name : String,
		@SerializedName("type") val type : String,
		@SerializedName("size") val size : Int
): Serializable