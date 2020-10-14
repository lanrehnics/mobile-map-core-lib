package com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips

import WhiteListedStop
import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.mqttmessage.KoboBusinessUnitTag
import java.io.Serializable

data class TripDetail (

        @SerializedName("tripId") val tripId : String,
        @SerializedName("tripReadId") val tripReadId : String,
        @SerializedName("overviewStatus") val overviewStatus : String ="",
        @SerializedName("status") val status : String,
        @SerializedName("statusHistory") val statusHistory : List<String>,
        @SerializedName("transportStatus") val transportStatus : String,
        @SerializedName("delivered") val delivered : Boolean,
        @SerializedName("expectedETA") val expectedETAObject : Eta?,
        @SerializedName("currentETA") val currentETAObject : Eta?,
        @SerializedName("totalDistance") val totalDistance : String,
        @SerializedName("remainingDistance") val remainingDistance : String,
        @SerializedName("whiteListedStop") val whiteListedStop : WhiteListedStop,
        @SerializedName("flagged") val flagged : Flagged,
        @SerializedName("flagHistory") val flagHistory : List<FlagHistory>,
        @SerializedName("blacklistedStops") val blacklistedStops : BlacklistedStops,
        @SerializedName("diverted") val diverted : Diverted,
        @SerializedName("goodType") val goodType : String,
        @SerializedName("goodCategory") val goodCategory : String,
        @SerializedName("partnerId") val partnerId : Int,
        @SerializedName("partnerName") val partnerName : String,
        @SerializedName("partnerEmail") val partnerEmail : String,
        @SerializedName("partnerMobile") val partnerMobile : String,
        @SerializedName("partnerPushToken") val partnerPushToken : String,
        @SerializedName("customerId") val customerId : Int,
        @SerializedName("customerName") val customerName : String,
        @SerializedName("customerMobile") val customerMobile : String,
        @SerializedName("customerEmail") val customerEmail : String,
        @SerializedName("customerPushToken") val customerPushToken : String,
        @SerializedName("driverId") val driverId : Int,
        @SerializedName("driverMobile") val driverMobile : String,
        @SerializedName("driverName") val driverName : String,
        @SerializedName("driverPushToken") val driverPushToken : String,
        @SerializedName("pickupLocation") val pickupLocation : PickupLocation,
        @SerializedName("deliveryLocation") val deliveryLocation : DeliveryLocation,
        @SerializedName("dropOffs") val dropOffs : List<DropOffs>,
        @SerializedName("sourceCountry") val sourceCountry : String,
        @SerializedName("deliveryCountry") val deliveryCountry : String,
        @SerializedName("requestCountry") val requestCountry : String,
        @SerializedName("source") val source : String,
        @SerializedName("destination") val destination : String,
        @SerializedName("travelledRoutePolyline") val travelledRoutePolyline : String="",
        @SerializedName("bestRoutePolyline") val bestRoutePolyline : String ="",
        @SerializedName("currentBestRoute") val currentBestRoute : String ="",
        @SerializedName("waybillNo") val waybillNo : String,
        @SerializedName("salesOrderNo") val salesOrderNo : String
//        @SerializedName("KoboBusinessUnitTag") val koboBusinessUnitTag : KoboBusinessUnitTag?
): Serializable