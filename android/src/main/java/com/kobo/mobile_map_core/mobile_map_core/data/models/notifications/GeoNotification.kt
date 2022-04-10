package com.kobo.mobile_map_core.mobile_map_core.data.models.notifications

import com.google.gson.annotations.SerializedName

data class GeoNotification (
		@SerializedName("status") val status : String,
		@SerializedName("readCount") val readCount : Int,
		@SerializedName("broadcast") val broadcast : Boolean,
		@SerializedName("_id") val _id : String,
		@SerializedName("key") val key : String,
		@SerializedName("tag") val tag : String,
		@SerializedName("platform") val platform : String,
		@SerializedName("title") val title : String,
		@SerializedName("userType") val userType : String,
		@SerializedName("message") val message : String,
		@SerializedName("notifType") val notifType : String,
		@SerializedName("sentStatus") val sentStatus : String,
		@SerializedName("readHistory") val readHistory : List<String>,
		@SerializedName("date") val date : String,
		@SerializedName("__v") val __v : Int
)