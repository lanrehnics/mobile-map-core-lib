package com.kobo.mobile_map_core.mobile_map_core.data.models.mqttmessage

import com.google.gson.annotations.SerializedName

data class AccountManager (

		@SerializedName("name") val name : String,
		@SerializedName("email") val email : String,
		@SerializedName("authId") val authId : Int,
		@SerializedName("phone") val phone : String,
		@SerializedName("pushNotif") val pushNotif : String,
		@SerializedName("desigId") val desigId : Int,
		@SerializedName("notifType") val notifType : NotifType
)