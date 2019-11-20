package com.kobo360.models

import com.google.gson.annotations.SerializedName

data class Data (

	@SerializedName("locations") val locations : List<Locations>
)