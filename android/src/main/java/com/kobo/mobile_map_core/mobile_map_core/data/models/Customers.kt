package com.kobo.mobile_map_core.mobile_map_core.data.models

import com.google.gson.annotations.SerializedName

data class Customers (

		@SerializedName("name") val name : String,
		@SerializedName("id") val id : Int
)