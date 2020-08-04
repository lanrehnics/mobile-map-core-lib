package com.kobo.mobile_map_core.mobile_map_core.data.repository

import com.google.android.libraries.maps.model.LatLng
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.ActiveTripsDataResponse
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiHelper
import com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model.AssetClassResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.AutoCompleteResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.AvailableTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.DedicatedTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview.OverviewResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.orders.AvailableOrdersResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.place_id.PlacesResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.reverse_geocode.ReverseGeocodeResponse
import io.reactivex.Single

class MainRepository(private val apiHelper: ApiHelper) {

    fun fetchPlacesByAutoComplete(searchTerms: String): Single<AutoCompleteResponse> {
        return apiHelper.fetchPlacesByAutoComplete(searchTerms)
    }

    fun fetchActiveTrips(userTypeAndId: String, filterBy: String?): Single<ActiveTripsDataResponse> {
        return apiHelper.fetchActiveTrips(userTypeAndId, filterBy)
    }

    fun fetchDedicatedTruck(userTypeAndId: String): Single<DedicatedTruckResponse> {
        return apiHelper.fetchDedicatedTruck(userTypeAndId)
    }

    fun fetchGroupedAssetClass(): Single<AssetClassResponse> {
        return apiHelper.fetchGroupedAssetClass()
    }

    fun getReverseGeocode(lat: Double, lng: Double): Single<ReverseGeocodeResponse> {
        return apiHelper.getReverseGeocode(lat, lng)
    }

    fun fetchAvailableTrucks(origin: LatLng?, destination: LatLng?, radius: Int?, assetId: String): Single<AvailableTruckResponse> {
        return apiHelper.fetchAvailableTrucks(origin, destination, radius, assetId)
    }

    fun fetchAvailableOrders(origin: LatLng?, assetType: String): Single<AvailableOrdersResponse> {
        return apiHelper.fetchAvailableOrders(origin, assetType)
    }


    fun placeId(placeId: String): Single<PlacesResponse> {
        return apiHelper.placeId(placeId)
    }

    fun fetchLocationOverview(userTypeAndId: String, latLng: LatLng): Single<OverviewResponse> {
        return apiHelper.fetchLocationOverview(userTypeAndId, latLng)
    }

}