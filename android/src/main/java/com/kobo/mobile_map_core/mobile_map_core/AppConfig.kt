package com.kobo.mobile_map_core.mobile_map_core

import com.google.android.libraries.maps.model.LatLng
import java.util.*

abstract class AppConfig {


    companion object {
//        private var BASEURL: String = ""
        private const val BASE_MOCK_GEO: String = "https://086c8d4c-7739-421a-839b-895bf1d5e436.mock.pstmn.io"
        private const val BASE_STAGE_GEO: String = "https://stagegpsapi.kobo360.com/v2"
        private const val BASESTAGGINGURL: String = "https://stage.api.kobo360.com"

        fun autoCompleteUrl(searchTerms: String): String {
            return "${BASE_STAGE_GEO}/location/autocomplete?input=${searchTerms}&source=google";
        }

        fun activeTripsUrl(userTypeAndId: String, filterBy: String?): String {
            return if (filterBy != null)
                "${BASE_STAGE_GEO}/location/activeTrips?$userTypeAndId&status=$filterBy"
            else
                "${BASE_STAGE_GEO}/location/activeTrips?$userTypeAndId"
        }

        fun dedicatedTruckUrl(userTypeAndId: String): String {
            return "${BASE_MOCK_GEO}/location/trucks?$userTypeAndId"
        }

        fun groupedAssetClass(): String {
            return "${BASESTAGGINGURL}/asset/grouped"
        }

        fun reverseGeocode(lat: Double, lng: Double): String {
            return "${BASE_STAGE_GEO}/location/reverse/geocode?lat=$lat&lng=$lng&source=google"
        }

        fun placeId(placesId: String): String {
            return "${BASE_STAGE_GEO}/location/place?placeId=$placesId"
        }

        fun availableTrucks(origin: LatLng?, destination: LatLng?, radius: Int?, assetId: String): String {
            val type = if(radius != null){
                "radius=$radius"
            }else{
                "destinationLat=${destination?.latitude}&destinationLng=${destination?.longitude}"
            }

            return "${BASE_STAGE_GEO}/location/availableTrucks?" +
                    "currentLat=${origin?.latitude}" +
                    "&currentLng=${origin?.longitude}" +
                    "&$type"+
                    "&assetType=$assetId"
        }

        fun locationOverview(userTypeAndId: String, latLng: LatLng): String {
            return "${BASE_STAGE_GEO}/location?$userTypeAndId&lat=${latLng.latitude}&lng=${latLng.longitude}"
        }
    }

}