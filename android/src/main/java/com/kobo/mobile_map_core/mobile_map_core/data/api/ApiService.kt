package com.kobo.mobile_map_core.mobile_map_core.data.api

import com.google.android.libraries.maps.model.LatLng
import com.kobo.mobile_map_core.mobile_map_core.data.models.BookTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.ActiveTripsDataResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model.AssetClassResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.AutoCompleteResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.AvailableTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.DedicatedTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.gsearch.GeneralSearchResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview.OverviewResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.orders.AvailableOrdersResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.place_id.PlacesResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.reverse_geocode.ReverseGeocodeResponse
import io.reactivex.Single

interface ApiService {
    fun fetchPlacesByAutoComplete(searchTerms: String): Single<AutoCompleteResponse>
    fun searchForData(query: MutableMap<String, String?>): Single<GeneralSearchResponse>
    fun fetchGroupedAssetClass(): Single<AssetClassResponse>
    fun getReverseGeocode(lat: Double, lng: Double): Single<ReverseGeocodeResponse>
    fun fetchAvailableTrucks(origin: LatLng?, destination: LatLng?, radius: Int?, assetId: String): Single<AvailableTruckResponse>
    fun fetchAvailableOrders(origin: LatLng?, assetType: String): Single<AvailableOrdersResponse>
    fun fetchLatLngFromPlacesId(placesId: String): Single<PlacesResponse>
    fun fetchActiveTrips(userTypeAndId: String, filterBy: String?): Single<ActiveTripsDataResponse>
    fun fetchDedicatedTruck(): Single<DedicatedTruckResponse>
    fun fetchLocationOverview(userTypeAndId: String, latLng: LatLng): Single<OverviewResponse>
    fun bookTruck(truckReg: String?): Single<BookTruckResponse>
}