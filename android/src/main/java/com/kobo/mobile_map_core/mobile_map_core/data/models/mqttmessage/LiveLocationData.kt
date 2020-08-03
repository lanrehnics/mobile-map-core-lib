package com.kobo.mobile_map_core.mobile_map_core.data.models.mqttmessage

import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Driver
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.LastKnownLocation
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Location
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.TripDetail
import com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model.AssetClass
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.ActivePartner

data class LiveLocationData (

		@SerializedName("regNumber") val regNumber : String?,
		@SerializedName("assetClass") val assetClass : AssetClass?,
		@SerializedName("available") val available : Boolean?,
		@SerializedName("bearing") val bearing : Double?,
		@SerializedName("speed") val speed : Double?,
		@SerializedName("millage") val millage : Int?,
		@SerializedName("geohash") val geohash : String?,
		@SerializedName("source") val source : String?,
		@SerializedName("lastKnownLocation") val lastKnownLocation : LastKnownLocation?,
		@SerializedName("locations") val locations : List<Location?>?,
		@SerializedName("driver") val driver : Driver?,
		@SerializedName("activePartner") val activePartner : ActivePartner?,
		@SerializedName("onTrip") val onTrip : Boolean?,
		@SerializedName("tripDetail") val tripDetail : TripDetail?
)