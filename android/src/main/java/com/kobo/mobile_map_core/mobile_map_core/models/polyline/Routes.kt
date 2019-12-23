package com.kobo360.models.polyline
import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.models.polyline.Bounds


data class Routes (

        @SerializedName("bounds") val bounds : Bounds,
        @SerializedName("copyrights") val copyrights : String,
        @SerializedName("legs") val legs : List<Legs>,
        @SerializedName("overview_polyline") val overview_polyline : OverviewPolyline,
        @SerializedName("summary") val summary : String,
        @SerializedName("warnings") val warnings : List<String>,
        @SerializedName("waypoint_order") val waypoint_order : List<String>
)