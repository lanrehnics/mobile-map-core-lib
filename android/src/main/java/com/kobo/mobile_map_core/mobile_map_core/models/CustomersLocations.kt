package com.kobo360.models

import com.google.gson.annotations.SerializedName

data class CustomersLocations(

        @SerializedName("data") val data: Data,
        @SerializedName("message") val message: String,
        @SerializedName("success") val success: Boolean
)