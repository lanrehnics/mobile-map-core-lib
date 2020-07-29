package com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips
import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.Locations
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.ActivePartner
import java.io.Serializable

data class Trips (
		@SerializedName("regNumber") val regNumber : String,
		@SerializedName("imei") val imei : String,
		@SerializedName("bearing") val bearing : Double,
		@SerializedName("speed") val speed : Double,
		@SerializedName("millage") val millage : Int,
		@SerializedName("geohash") val geohash : String,
		@SerializedName("source") val source : String,
		@SerializedName("provider") val provider : String,
		@SerializedName("available") val available : Boolean,
		@SerializedName("onTrip") val onTrip : Boolean,
		@SerializedName("stopped") val stopped : Boolean,
		@SerializedName("diverted") val diverted : Boolean,
		@SerializedName("lastKnownLocation") val lastKnownLocation : LastKnownLocation?,
		@SerializedName("lastFiveLocations") val lastFiveLocations : List<LastFiveLocations>,
		@SerializedName("lastConnectionTime") val lastConnectionTime : String,
		@SerializedName("assetClass") val assetClass : Asset?,
		@SerializedName("activePartner") val activePartner : ActivePartner?,
		@SerializedName("activePartnerId") val activePartnerId : Int,
		@SerializedName("activePartnerName") val activePartnerName : String,
		@SerializedName("driver") val driver : Driver,
		@SerializedName("events") val events : List<Events>,
		@SerializedName("tripDetail") val  tripDetail : TripDetail?
) : Serializable