package com.kobo.mobile_map_core.mobile_map_core.data.models

import com.google.gson.annotations.SerializedName

data class BookAction (

		@SerializedName("name") val name : String,
		@SerializedName("email") val email : String,
		@SerializedName("userId") val userId : Int,
		@SerializedName("userType") val userType : String,
		@SerializedName("date") val date : String,
		@SerializedName("action") val action : String,
		@SerializedName("unbookedReason") val unbookedReason : String
)