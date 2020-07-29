package com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview

import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.LastKnownLocation
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Location
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import java.io.Serializable

data class Overview(
        @SerializedName("location") val location: Location?,
        @SerializedName("lastKnownLocation") val lastKnownLocation: LastKnownLocation,
        @SerializedName("totalAvailableTrucks") val totalAvailableTrucks: Int?,
        @SerializedName("totalActiveTrucks") val totalActiveTrucks: Int?,
        @SerializedName("trucks") val trucks: List<Trucks?>,
        @SerializedName("customerLocations") val customerLocations: List<CustomerLocations?>?,
        @SerializedName("kobocareStations") val kobocareStations: List<KobocareStations?>?
) : Serializable