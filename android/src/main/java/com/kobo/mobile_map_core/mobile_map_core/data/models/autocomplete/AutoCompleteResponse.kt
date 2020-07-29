package com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete

import AutoCompleteData
import com.google.gson.annotations.SerializedName

data class AutoCompleteResponse (
        @SerializedName("success") val success: Boolean,
        @SerializedName("message") val message: String,
        @SerializedName("data") val data: AutoCompleteData
)