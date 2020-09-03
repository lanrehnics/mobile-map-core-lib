package com.kobo.mobile_map_core.mobile_map_core.data.models.gsearch

import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks

data class GeneralSearchData(

		@SerializedName("result") val result: List<Trucks>
)