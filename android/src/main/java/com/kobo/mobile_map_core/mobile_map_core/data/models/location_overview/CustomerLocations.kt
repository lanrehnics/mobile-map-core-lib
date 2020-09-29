package com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview

import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Location
import java.io.Serializable

data class CustomerLocations(

        @SerializedName("_id") val _id: String?,
        @SerializedName("geofenceRadius") val geofenceRadius: Int?,
        @SerializedName("type") val type: String?,
        @SerializedName("facilityMapping") val facilityMapping: List<String>?,
        @SerializedName("customerId") val customerId: Int?,
        @SerializedName("customerName") val customerName: String?,
        @SerializedName("locationName") val locationName: String?,
        @SerializedName("state") val state: String?,
        @SerializedName("country") val country: String?,
        @SerializedName("contactPhone") val contactPhone: String?,
        @SerializedName("contactName") val contactName: String?,
        @SerializedName("location") val location: Location?,
        @SerializedName("__v") val __v: Int?,
        @SerializedName("distanceToPoint") val distanceToPoint: Double?
) : Serializable