package com.kobo.mobile_map_core.mobile_map_core.data.models.orders

import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.DeliveryLocation
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Eta
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.PickupLocation
import com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model.AssetClass

data class Orders(

        @SerializedName("_id") val _id: String?,
        @SerializedName("dropoffs") val dropoffs: List<String?>?,
        @SerializedName("id") val id: String,
        @SerializedName("customerId") val customerId: Int?,
        @SerializedName("customerName") val customerName: String?,
        @SerializedName("pickupLocation") val pickupLocation: PickupLocation?,
        @SerializedName("pickupGeohash") val pickupGeohash: String?,
        @SerializedName("assetClass") val assetClass: AssetClass?,
        @SerializedName("deliveryLocation") val deliveryLocation: DeliveryLocation?,
        @SerializedName("deliveryGeohash") val deliveryGeohash: String?,
        @SerializedName("expectedETA") val expectedETA: Eta?,
        @SerializedName("totalDistance") val totalDistance: Int?,
        @SerializedName("__v") val __v: Int?,
        @SerializedName("distanceToPoint") val distanceToPoint: Double?
)