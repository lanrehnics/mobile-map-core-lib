package com.kobo.mobile_map_core.mobile_map_core.data.api

import android.content.Context
import android.util.ArrayMap
import androidx.preference.PreferenceManager
import com.google.android.libraries.maps.model.LatLng
import com.google.gson.Gson
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.ActiveTripsDataResponse
import com.kobo.mobile_map_core.mobile_map_core.AppConfig
import com.kobo.mobile_map_core.mobile_map_core.MobileMapCorePlugin
import com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model.AssetClassResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.AutoCompleteResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.AvailableTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.DedicatedTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview.OverviewResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.orders.AvailableOrdersResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.place_id.PlacesResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.reverse_geocode.ReverseGeocodeResponse
import com.kobo.mobile_map_core.mobile_map_core.utils.client.LiteHttp
import com.kobo360.models.polyline.PolylineJsonDecoder
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Single
import io.reactivex.internal.operators.observable.ObservableSingleSingle
import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.rx2.rxSingle
import java.lang.Exception

class ApiServiceImplWithLiteHttp(val context: Context) : ApiService {


    private fun getToken(): String? {
        val shaper = this.let {
            PreferenceManager.getDefaultSharedPreferences(context)
        }
        return shaper.getString(MobileMapCorePlugin.KEY_AUTH_TOKEN, "")
    }


    override fun fetchPlacesByAutoComplete(searchTerms: String): Single<AutoCompleteResponse> {
        return Rx2AndroidNetworking.get(AppConfig.autoCompleteUrl(context, searchTerms))
                .addHeaders("Authorization", getToken())
                .build()
                .getObjectSingle(AutoCompleteResponse::class.java)
    }

    override fun fetchActiveTrips(userTypeAndId: String, filterBy: String?): Single<ActiveTripsDataResponse> {
        return Rx2AndroidNetworking.get(AppConfig.activeTripsUrl(context, userTypeAndId, filterBy))
                .addHeaders("Authorization", getToken())
                .build()
                .getObjectSingle(ActiveTripsDataResponse::class.java)
    }

    override fun fetchDedicatedTruck(userTypeAndId: String): Single<DedicatedTruckResponse> {
        return Rx2AndroidNetworking.get(AppConfig.dedicatedTruckUrl(context, userTypeAndId))
                .addHeaders("Authorization", getToken())
                .build()
                .getObjectSingle(DedicatedTruckResponse::class.java)
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


//    override fun getReverseGeocode(lat: Double, lng: Double): Single<ReverseGeocodeResponse> {
//        return rxSingle {
//            try {
//
//                val headers: ArrayMap<String, String> = ArrayMap()
//                headers["Authorization"] = getToken()
//
//                val res = LiteHttp.instance?.getWithHeader(AppConfig.reverseGeocode(context, lat, lng), headers)
//                print(res.toString())
//                val result = Gson().fromJson(res.toString(), ReverseGeocodeResponse::class.java)
//                result
//            } catch (e: Exception) {
//                throw  e
//            }
//        }
//    }


    override fun fetchAvailableTrucks(origin: LatLng?, destination: LatLng?, radius: Int?, assetId: String): Single<AvailableTruckResponse> {
        return Rx2AndroidNetworking.get(
                AppConfig.availableTrucks(context, origin
                        , destination, radius, assetId))
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

}