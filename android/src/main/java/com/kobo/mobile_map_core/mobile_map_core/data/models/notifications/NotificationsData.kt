package com.kobo.mobile_map_core.mobile_map_core.data.models.notifications

import com.google.gson.annotations.SerializedName
data class NotificationsData (
		@SerializedName("currentPage") val currentPage : Int,
		@SerializedName("totalPages") val totalPages : Int,
		@SerializedName("limit") val limit : Int,
		@SerializedName("total") val total : Int,
		@SerializedName("notification") val notification : List<GeoNotification>
)