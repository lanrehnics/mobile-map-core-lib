package com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks

import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.*
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.ActivePartner
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.LastTenLocations
import java.io.Serializable

data class Trucks(

        @SerializedName("_id") val _id: String?,
        @SerializedName("regNumber") val regNumber: String?,
        @SerializedName("radiusInKM") val radiusInKM: Int?,
        @SerializedName("polyline") val polyline: String?,
        @SerializedName("imei") val imei: String?,
        @SerializedName("bearing") val bearing: Double?,
        @SerializedName("speed") val speed: Double?,
        @SerializedName("millage") val millage: Int?,
        @SerializedName("geohash") val geohash: String?,
        @SerializedName("source") val source: String?,
        @SerializedName("provider") val provider: String?,
        @SerializedName("available") val available: Boolean?,
        @SerializedName("stopped") val stopped: Boolean?,
        @SerializedName("diverted") val diverted: Boolean?,
        @SerializedName("locations") val locations: List<Location?>?,
        @SerializedName("lastKnownLocation") val lastKnownLocation: LastKnownLocation?,
        @SerializedName("lastConnectionTime") val lastConnectionTime: String?,
        @SerializedName("assetClass") val assetClass: Asset?,
        @SerializedName("activePartner") val activePartner: ActivePartner?,
        @SerializedName("driver") val driver: Driver?,
        @SerializedName("events") val events: List<Events?>?,
        @SerializedName("tripDetail") val tripDetail: TripDetail?,
        @SerializedName("onTrip") val onTrip: Boolean?,
        @SerializedName("createdAt") val createdAt: String?,
        @SerializedName("updatedAt") val updatedAt: String?,
        @SerializedName("__v") val __v: Int?,
        @SerializedName("distanceToPoint") val distanceToPoint: Double?
) :Serializable