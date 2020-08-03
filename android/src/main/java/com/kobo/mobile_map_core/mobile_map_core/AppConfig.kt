package com.kobo.mobile_map_core.mobile_map_core

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.android.libraries.maps.model.LatLng
import java.util.*

abstract class AppConfig {


    companion object {


        private fun getBaseUrl(context: Context): String? {
            val shaper = this.let {
                PreferenceManager.getDefaultSharedPreferences(context)
            }
            return shaper.getString(MobileMapCorePlugin.KEY_GEO_BASE_URL, "")
        }

        private fun getKoboBaseUrl(context: Context): String? {
            val shaper = this.let {
                PreferenceManager.getDefaultSharedPreferences(context)
            }
            return shaper.getString(MobileMapCorePlugin.KEY_KOBO_BASE_URL, "")
        }


        //        private const val getBaseUrl(context): String =getBaseUrl()
//        private const val BASESTAGGINGURL: String = "https://stage.api.kobo360.com"

        fun autoCompleteUrl(context: Context, searchTerms: String): String {
            return "${getBaseUrl(context)}location/autocomplete?input=${searchTerms}&source=google";
        }

        fun activeTripsUrl(context: Context, userTypeAndId: String, filterBy: String?): String {
            return if (filterBy != null)
                "${getBaseUrl(context)}location/activeTrips?$userTypeAndId&status=$filterBy"
            else
                "${getBaseUrl(context)}location/activeTrips?$userTypeAndId"
        }

        fun dedicatedTruckUrl(context: Context, userTypeAndId: String): String {
            return "${getBaseUrl(context)}location/trucks?$userTypeAndId"
        }

        fun groupedAssetClass(context: Context): String {
            return "${getKoboBaseUrl(context)}asset/grouped"
        }

        fun reverseGeocode(context: Context, lat: Double, lng: Double): String {
            return "${getBaseUrl(context)}location/reverse/geocode?lat=$lat&lng=$lng&source=google"
        }

        fun placeId(context: Context, placesId: String): String {
            return "${getBaseUrl(context)}location/place?placeId=$placesId"
        }

        fun availableTrucks(context: Context, origin: LatLng?, destination: LatLng?, radius: Int?, assetId: String): String {
            val type = if (radius != null) {
                "radius=$radius"
            } else {
                "destinationLat=${destination?.latitude}&destinationLng=${destination?.longitude}"
            }

            return "${getBaseUrl(context)}location/availableTrucks?" +
                    "currentLat=${origin?.latitude}" +
                    "&currentLng=${origin?.longitude}" +
                    "&$type" +
                    "&assetType=$assetId"
        }

        fun locationOverview(context: Context, userTypeAndId: String, latLng: LatLng): String {
            return "${getBaseUrl(context)}location?$userTypeAndId&lat=${latLng.latitude}&lng=${latLng.longitude}"
        }
    }

}