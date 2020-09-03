package com.kobo.mobile_map_core.mobile_map_core.data.services

import android.content.Context
import android.preference.PreferenceManager.*
import android.util.ArrayMap
import android.util.Log
import com.google.android.libraries.maps.model.LatLng
import com.google.gson.Gson
import com.kobo.mobile_map_core.mobile_map_core.MobileMapCorePlugin
import com.kobo.mobile_map_core.mobile_map_core.utils.client.LiteHttp
import com.kobo.mobile_map_core.mobile_map_core.enums.AppType
import com.kobo.mobile_map_core.mobile_map_core.data.models.KoboLocations
import com.kobo360.models.polyline.PolylineJsonDecoder
import java.util.*

class MapService constructor(private val mContext: Context) {

    private val geoBaseUrl =
            "https://maps.googleapis.com/maps/api/directions/json?"
    private val googleMapApiKey: String = "AIzaSyCZsmroa6P94FfCXaWIlVd9PcVpVpQYdqs"

    suspend fun getPolyline(origin: LatLng, destination: LatLng): List<LatLng> {
        var polyLineList: List<LatLng> = ArrayList()
        val res = LiteHttp.instance?.get(getMapRouteUrlByTrip(origin, destination))
//        print(res.toString())
        val result = Gson().fromJson(res.toString(), PolylineJsonDecoder::class.java)
        if (result.routes.isNotEmpty()) {
            polyLineList = decodePoly(result.routes[0].overview_polyline.points)
        }
        return polyLineList
    }

    private fun getMapRouteUrlByTrip(origin: LatLng, destination: LatLng): String {
        return "${geoBaseUrl}origin=" +
                origin.latitude.toString() +
                "," +
                origin.longitude.toString() +
                "&destination=" +
                destination.latitude.toString() +
                "," +
                destination.longitude.toString() +
                "&mode=driving" +
                "&key=${googleMapApiKey}"
    }

    fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                    lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5
            )
            poly.add(p)
        }

        return poly
    }

//    suspend fun fetchCustomersLocations(geoHash: String): KoboLocations? {
//
//        var customersLocations: KoboLocations? = null
//
//        val customerUrl: String? = getDefaultSharedPreferences(mContext).getString(MobileMapCorePlugin.KEY_KOBO_CUSTOMER_URL, null)
//        val token: String? = getDefaultSharedPreferences(mContext).getString(MobileMapCorePlugin.KEY_AUTH_TOKEN, null)
//        val appType: String? = getDefaultSharedPreferences(mContext).getString(MobileMapCorePlugin.KEY_APP_TYPE, null)
//
//        if (customerUrl != null && customerUrl.isNotEmpty()) {
//            val headers: ArrayMap<String, String> = ArrayMap()
//            headers["Authorization"] = token
//            val res = LiteHttp.instance?.get(
//                    when (appType?.let { AppType.valueOf(it.toUpperCase(Locale.getDefault())) }) {
//                        AppType.SQUAD -> "$customerUrl?geohash=$geoHash"
//                        else -> customerUrl
//                    }
//                    , headers, null)
//            customersLocations = Gson().fromJson(res, KoboLocations::class.java)
//        } else {
//            Log.e("customerUrl:  ", "No SET")
//        }
//        return customersLocations
//    }

//    suspend fun fetchKoboStations(geoHash: String): KoboLocations? {
//
//        var koboStations: KoboLocations? = null
//
//        val stationUrl: String? = getDefaultSharedPreferences(mContext).getString(MobileMapCorePlugin.KEY_KOBO_STATIONS_URL, null)
//        val token: String? = getDefaultSharedPreferences(mContext).getString(MobileMapCorePlugin.KEY_AUTH_TOKEN, null)
//        val appType: String? = getDefaultSharedPreferences(mContext).getString(MobileMapCorePlugin.KEY_APP_TYPE, null)
//
//        if (stationUrl != null && stationUrl.isNotEmpty()) {
//
//
//            val headers: ArrayMap<String, String> = ArrayMap()
//            headers["Authorization"] = token
//            val res = LiteHttp.instance?.get(
//                    when (appType?.let { AppType.valueOf(it.toUpperCase(Locale.getDefault())) }) {
//                        AppType.SQUAD -> "$stationUrl?geohash=$geoHash"
//                        else -> stationUrl
//                    }, headers, null)
//            koboStations = Gson().fromJson(res, KoboLocations::class.java)
//        } else {
//            Log.e("koboStationUrl:  ", "No SET")
//        }
//        return koboStations
//    }

}