package com.kobo.mobile_map_core.mobile_map_core.data.models.mqttmessage

import com.google.gson.annotations.SerializedName

data class Parameters (

		@SerializedName("customerId") val customerId : Int,
		@SerializedName("customerName") val customerName : String,
		@SerializedName("customerAccountId") val customerAccountId : Int,
		@SerializedName("customerAccountName") val customerAccountName : String
)