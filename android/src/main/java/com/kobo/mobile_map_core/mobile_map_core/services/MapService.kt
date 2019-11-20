package com.kobo.mobile_map_core.mobile_map_core.services

import android.content.Context
import android.preference.PreferenceManager.*
import android.util.ArrayMap
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import com.kobo.mobile_map_core.mobile_map_core.MobileMapCorePlugin
import com.kobo.mobile_map_core.mobile_map_core.client.RestClientHelper
import com.kobo.mobile_map_core.mobile_map_core.map.MapsActivity
import com.kobo360.models.polyline.PolylineJsonDecoder
import com.kobo360.models.CustomersLocations
import java.util.ArrayList

class MapService constructor(private val mContext: Context) {

    private val geoBaseUrl =
            "https://maps.googleapis.com/maps/api/directions/json?"
    private val googleMapApiKey: String = "AIzaSyCZsmroa6P94FfCXaWIlVd9PcVpVpQYdqs"

    suspend fun getPolyline(origin: GeoPoint, destination: GeoPoint): List<LatLng> {
        var polyLineList: List<LatLng> = ArrayList()
        val res = RestClientHelper.instance?.get(getMapRouteUrlByTrip(origin, destination))
        print(res.toString())
        val result = Gson().fromJson(res.toString(), PolylineJsonDecoder::class.java)
        if (result.routes.isNotEmpty()) {
            polyLineList = decodePoly(result.routes[0].overview_polyline.points)
        }
        return polyLineList
    }

    private fun getMapRouteUrlByTrip(origin: GeoPoint, destination: GeoPoint): String {
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

    private fun decodePoly(encoded: String): List<LatLng> {
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

    suspend fun fetchCustomersLocations(): CustomersLocations? {

        var customersLocations: CustomersLocations? = null

        val url: String? = getDefaultSharedPreferences(mContext).getString(MapsActivity.KEY_CUSTOMER_STATION_URL, null)
        val token: String? = getDefaultSharedPreferences(mContext).getString(MobileMapCorePlugin.KEY_AUTH_TOKEN, null)
        val custormerId: String? = getDefaultSharedPreferences(mContext).getString(MobileMapCorePlugin.KEY_CUSTOMER_ID, null)

        if (url != null) {
            val headers: ArrayMap<String, String> = ArrayMap()
            headers["Authorization"] = token
            val res = RestClientHelper.instance?.get(url, headers, null)
            customersLocations = Gson().fromJson(res, CustomersLocations::class.java)

        } else {
            Log.e("url:  ", "No URL SET")
        }
        return customersLocations
    }

}