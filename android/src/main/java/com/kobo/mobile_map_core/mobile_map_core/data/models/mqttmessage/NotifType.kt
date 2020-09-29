package com.kobo.mobile_map_core.mobile_map_core.data.models.mqttmessage

import com.google.gson.annotations.SerializedName

data class NotifType (

		@SerializedName("sms") val sms : Boolean?,
		@SerializedName("email") val email : Boolean?,
		@SerializedName("push") val push : Boolean?
)