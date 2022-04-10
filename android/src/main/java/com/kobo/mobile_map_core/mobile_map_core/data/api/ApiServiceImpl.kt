package com.kobo.mobile_map_core.mobile_map_core.data.api

import android.content.Context
import android.util.ArrayMap
import androidx.preference.PreferenceManager
import com.google.android.libraries.maps.model.LatLng
import com.google.gson.Gson
import com.kobo.mobile_map_core.mobile_map_core.AppConfig
import com.kobo.mobile_map_core.mobile_map_core.MobileMapCorePlugin
import com.kobo.mobile_map_core.mobile_map_core.data.models.BookTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.ActiveTripsDataResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model.AssetClassResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.AutoCompleteResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.AvailableTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.DedicatedTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.gsearch.GeneralSearchResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview.OverviewResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.notifications.NotificationsResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.orders.AvailableOrdersResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.place_id.PlacesResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.reverse_geocode.ReverseGeocodeResponse
import com.kobo.mobile_map_core.mobile_map_core.utils.client.LiteHttp
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Single
import kotlinx.coroutines.rx2.rxSingle

class ApiServiceImpl(val context: Context) : ApiService {


    private fun getToken(): String? {
        val shaper = this.let {
            PreferenceManager.getDefaultSharedPreferences(context)
        }
        return shaper.getString(MobileMapCorePlugin.KEY_AUTH_TOKEN, "")
    }

    private fun getAppType(): String? {
        val shaper = this.let {
            PreferenceManager.getDefaultSharedPreferences(context)
        }
        return shaper.getString(MobileMapCorePlugin.KEY_APP_TYPE, "")
    }

    private fun getUserId(): String? {
        val shaper = this.let {
            PreferenceManager.getDefaultSharedPreferences(context)
        }
        return shaper.getInt(MobileMapCorePlugin.KEY_ID, -1).toString()
    }


    override fun searchForData(query: MutableMap<String, String?>): Single<GeneralSearchResponse> {
        query["limit"] = "10"
        if ((getAppType() == MobileMapCorePlugin.APP_TYPE_TRANSPORTER)
                || (getAppType() == MobileMapCorePlugin.APP_TYPE_PARTNER)) {
            query["partnerId"] = getUserId()
        }
        return Rx2AndroidNetworking.get(AppConfig.searchForData(context))
                .addHeaders("Authorization", getToken())
                .addQueryParameter(query)
                .build()
                .getObjectSingle(GeneralSearchResponse::class.java)

//                .addQueryParameter("searchTerm",query["searchTerm"])
    }

    override fun fetchPlacesByAutoComplete(searchTerms: String): Single<AutoCompleteResponse> {
        return Rx2AndroidNetworking.get(AppConfig.autoCompleteUrl(context, searchTerms))
                .addHeaders("Authorization", getToken())
                .build()
                .getObjectSingle(AutoCompleteResponse::class.java)
    }

    override fun fetchActiveTrips(userTypeAndId: String, filterBy: String?): Single<ActiveTripsDataResponse> {
        val query: MutableMap<String, String?> = if ((getAppType() == MobileMapCorePlugin.APP_TYPE_TRANSPORTER)
                || (getAppType() == MobileMapCorePlugin.APP_TYPE_PARTNER))
            mutableMapOf("partnerId" to getUserId())
        else
            mutableMapOf("customerId" to getUserId())

        filterBy?.let { query["status"] = filterBy }


        return Rx2AndroidNetworking.get(AppConfig.activeTripsUrl(context))
                .addHeaders("Authorization", getToken())
                .addQueryParameter(query)
                .build()
                .getObjectSingle(ActiveTripsDataResponse::class.java)
    }


//    override fun fetchDedicatedTruck(): Single<DedicatedTruckResponse> {
//
//        val query: MutableMap<String, String?> = mutableMapOf("customerId" to getUserId())
//
//        if ((getAppType() == MobileMapCorePlugin.APP_TYPE_TRANSPORTER)
//                || (getAppType() == MobileMapCorePlugin.APP_TYPE_PARTNER)) {
//            query["partnerId"] = getUserId()
//        }
//
//        return Rx2AndroidNetworking.get(AppConfig.dedicatedTruckUrl(context))
//                .addHeaders("Authorization", getToken())
//                .addQueryParameter(query)
//                .build()
//                .getObjectSingle(DedicatedTruckResponse::class.java)
//    }


    override fun fetchDedicatedTruck(): Single<DedicatedTruckResponse> {
        val query: ArrayMap<String, Any>? = ArrayMap()
        if ((getAppType() == MobileMapCorePlugin.APP_TYPE_TRANSPORTER)
                || (getAppType() == MobileMapCorePlugin.APP_TYPE_PARTNER)) {
            query?.set("partnerId", getUserId())
        } else {
            query?.set("customerId", getUserId())
        }

        return rxSingle {
            try {

                val headers: ArrayMap<String, String> = ArrayMap()
                headers["Authorization"] = getToken()

                val res = LiteHttp.instance?.get(AppConfig.dedicatedTruckUrl(context), headers, query)
                val result = Gson().fromJson(res.toString(), DedicatedTruckResponse::class.java)
                result
            } catch (e: Exception) {
                throw  e
            }
        }
    }

    override fun fetchGroupedAssetClass(): Single<AssetClassResponse> {
        return Rx2AndroidNetworking.get(AppConfig.groupedAssetClass(context))
                .addHeaders("Authorization", getToken())
                .build()
                .getObjectSingle(AssetClassResponse::class.java)
    }

    override fun getReverseGeocode(lat: Double, lng: Double): Single<ReverseGeocodeResponse> {
        return Rx2AndroidNetworking.get(AppConfig.reverseGeocode(context, lat, lng))
                .addHeaders("Authorization", getToken())
                .build()
                .getObjectSingle(ReverseGeocodeResponse::class.java)
    }


    override fun fetchAvailableTrucks(origin: LatLng?, destination: LatLng?, radius: Int?, assetId: String): Single<AvailableTruckResponse> {
        return Rx2AndroidNetworking.get(
                AppConfig.availableTrucks(context, origin, destination, radius, assetId))
                .addHeaders("Authorization", getToken())
                .build()
                .getObjectSingle(AvailableTruckResponse::class.java)
    }

    override fun fetchAvailableOrders(origin: LatLng?, assetType: String): Single<AvailableOrdersResponse> {
        return Rx2AndroidNetworking.get(
                AppConfig.availableOrders(context, origin, assetType))
                .addHeaders("Authorization", getToken())
                .build()
                .getObjectSingle(AvailableOrdersResponse::class.java)
    }

    override fun fetchLatLngFromPlacesId(placesId: String): Single<PlacesResponse> {
        return Rx2AndroidNetworking.get(AppConfig.placeId(context, placesId))
                .addHeaders("Authorization", getToken())
                .build()
                .getObjectSingle(PlacesResponse::class.java)
    }

    override fun fetchLocationOverview(userTypeAndId: String, latLng: LatLng): Single<OverviewResponse> {
        return Rx2AndroidNetworking.get(AppConfig.locationOverview(context, userTypeAndId, latLng))
                .addHeaders("Authorization", getToken())
                .build()
                .getObjectSingle(OverviewResponse::class.java)
    }

//    override fun fetchLocationOverview(userTypeAndId: String, latLng: LatLng): Single<OverviewResponse> {
//
//        return rxSingle {
//            try {
//                val headers: ArrayMap<String, String> = ArrayMap()
//                headers["Authorization"] = getToken()
//
//                val res = LiteHttp.instance?.get(AppConfig.locationOverview(context, userTypeAndId, latLng), headers, null)
//                val result = Gson().fromJson(res.toString(), OverviewResponse::class.java)
//                result
//            } catch (e: Exception) {
//                throw  e
//            }
//        }
//    }


    override fun bookTruck(truckReg: String?): Single<BookTruckResponse> {

        return rxSingle {
            try {
                if (truckReg.isNullOrEmpty()) {
                    throw Throwable(message = "Truck reg cannot be empty")
                }
                val headers: ArrayMap<String, String> = ArrayMap()
                headers["Authorization"] = getToken()

                val res = LiteHttp.instance?.put(AppConfig.bookTruck(context, truckReg
                        ?: ""), headers, null)
                val result = Gson().fromJson(res.toString(), BookTruckResponse::class.java)
                result
            } catch (e: Exception) {
                throw  e
            }
        }
    }


    override fun fetchNotifications(): Single<NotificationsResponse> {

        return rxSingle {
            try {
                val headers: ArrayMap<String, String> = ArrayMap()
                headers["Authorization"] = getToken()

                val query: ArrayMap<String, Any>? = ArrayMap()
                
                query?.set("userType", "admin")
                query?.set("tag", "TRIP_STATUS")
                query?.set("key", "ACTION")

                val res = LiteHttp.instance?.get(AppConfig.notifications(context), headers, query)
                val result = Gson().fromJson(res.toString(), NotificationsResponse::class.java)
                result
            } catch (e: Exception) {
                throw  e
            }
        }
    }

}