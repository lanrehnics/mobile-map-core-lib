package com.kobo.mobile_map_core.mobile_map_core.data.models.orders

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AvailableOrdersData (
	@SerializedName("orders") val orders : List<Orders>?
):Serializable