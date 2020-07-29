package com.kobo.mobile_map_core.mobile_map_core.data.models


import com.google.gson.annotations.SerializedName

data class Locations (

		@SerializedName("id") val id : Int,
		@SerializedName("customer_id") val customer_id : Int,
		@SerializedName("name") val name : String,
		@SerializedName("address") val address : String,
		@SerializedName("state") val state : String,
		@SerializedName("lat") val lat : Double,
		@SerializedName("long") val long : Double,
		@SerializedName("created_at") val created_at : String,
		@SerializedName("updated_at") val updated_at : String,
		@SerializedName("contact_name") val contact_name : String,
		@SerializedName("contact_phone") val contact_phone : String
)