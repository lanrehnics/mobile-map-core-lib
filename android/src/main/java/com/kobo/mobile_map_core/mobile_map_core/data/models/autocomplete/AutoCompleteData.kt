package com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete

import com.google.gson.annotations.SerializedName

data class AutoCompleteData (
		@SerializedName("autocomplete") val autocomplete : List<Autocomplete>
)