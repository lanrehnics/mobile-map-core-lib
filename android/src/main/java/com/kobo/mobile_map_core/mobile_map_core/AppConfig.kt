package com.kobo.mobile_map_core.mobile_map_core

import android.content.Context
import android.util.Log
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

        fun searchForData(context: Context): String {
            return "${getBaseUrl(context)}location/search";
        }

        fun activeTripsUrl(context: Context): String {
            return "${getBaseUrl(context)}location/activeTrips"
        }

        fun dedicatedTruckUrl(context: Context): String {
            return "${getBaseUrl(context)}location/trucks"
        }

        fun bookTruck(context: Context, truckRegistrationNumber: String): String {
            return "${getBaseUrl(context)}bookTruck/$truckRegistrationNumber"
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


        fun availableOrders(context: Context, origin: LatLng?, assetType: String): String {
            return "${getBaseUrl(context)}location/availableOrder?lat=${origin?.latitude}&lng=${origin?.longitude}&assetType=$assetType"
        }

        fun locationOverview(context: Context, userTypeAndId: String, latLng: LatLng): String {
            return "${getBaseUrl(context)}location?$userTypeAndId&lat=${latLng.latitude}&lng=${latLng.longitude}"
        }
    }

}