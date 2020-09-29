package com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class FlaggedBy (

		@SerializedName("name") val name : String,
		@SerializedName("email") val email : String
): Serializable