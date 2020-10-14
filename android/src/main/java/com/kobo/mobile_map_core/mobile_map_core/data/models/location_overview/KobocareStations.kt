package com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview
import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Location
import java.io.Serializable

data class KobocareStations (

        @SerializedName("_id") val _id : String?,
        @SerializedName("products") val products : List<Products?>,
        @SerializedName("vendorId") val vendorId : Int?,
        @SerializedName("vendorName") val vendorName : String?,
        @SerializedName("stationId") val stationId : Int?,
        @SerializedName("stationName") val stationName : String?,
        @SerializedName("address") val address : String?,
        @SerializedName("stateCode") val stateCode : Int?,
        @SerializedName("state") val state : String?,
        @SerializedName("country") val country : String? =null,
        @SerializedName("location") val location : Location?=null,
        @SerializedName("contactName") val contactName : String?,
        @SerializedName("contactPhone") val contactPhone : String?,
        @SerializedName("geofenceRadius") val geofenceRadius : Int?,
        @SerializedName("__v") val __v : Int?,
        @SerializedName("distanceToPoint") val distanceToPoint : Double?
):Serializable