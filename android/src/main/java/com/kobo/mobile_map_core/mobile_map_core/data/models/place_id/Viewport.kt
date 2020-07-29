package com.kobo.mobile_map_core.mobile_map_core.data.models.place_id

import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.place_id.Northeast
import com.kobo.mobile_map_core.mobile_map_core.data.models.place_id.Southwest


data class Viewport (

        @SerializedName("northeast") val northeast : Northeast,
        @SerializedName("southwest") val southwest : Southwest
)