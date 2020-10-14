package com.kobo.mobile_map_core.mobile_map_core.data.api

import com.google.android.libraries.maps.model.LatLng


class ApiHelper(private val apiService: ApiService) {
    fun fetchPlacesByAutoComplete(searchTerms: String) = apiService.fetchPlacesByAutoComplete(searchTerms)
    fun searchForData(query: MutableMap<String, String?>) = apiService.searchForData(query)
    fun fetchActiveTrips(userTypeAndId: String, filterBy: String?) = apiService.fetchActiveTrips(userTypeAndId, filterBy)
    fun fetchDedicatedTruck() = apiService.fetchDedicatedTruck()
    fun fetchGroupedAssetClass() = apiService.fetchGroupedAssetClass()
    fun getReverseGeocode(lat: Double, lng: Double) = apiService.getReverseGeocode(lat, lng)
    fun fetchAvailableTrucks(origin: LatLng?, destination: LatLng?, radius: Int?, assetId: String) = apiService.fetchAvailableTrucks(origin, destination, radius, assetId)
    fun fetchAvailableOrders(origin: LatLng?, assetType: String) = apiService.fetchAvailableOrders(origin, assetType)
    fun placeId(placeId: String) = apiService.fetchLatLngFromPlacesId(placeId)
    fun fetchLocationOverview(userTypeAndId: String, latLng: LatLng) = apiService.fetchLocationOverview(userTypeAndId, latLng)
    fun bookTruck(truckReg: String?) = apiService.bookTruck(truckReg)

}