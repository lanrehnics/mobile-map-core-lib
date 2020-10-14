package com.kobo.mobile_map_core.mobile_map_core.data.models

import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.Autocomplete

data class SelectedAddrss(
        var pickUp: Autocomplete?,
        var destination: Autocomplete?,
        var mode: Int? = 0
)