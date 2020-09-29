package com.kobo.mobile_map_core.mobile_map_core.data.models

import com.google.gson.annotations.SerializedName

data class BookedBy (

		@SerializedName("_id") val _id : String,
		@SerializedName("name") val name : String,
		@SerializedName("email") val email : String,
		@SerializedName("userId") val userId : Int,
		@SerializedName("userType") val userType : String
)