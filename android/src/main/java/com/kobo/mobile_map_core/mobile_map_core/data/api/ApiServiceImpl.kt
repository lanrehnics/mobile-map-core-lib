package com.kobo.mobile_map_core.mobile_map_core.data.api

import com.google.android.libraries.maps.model.LatLng
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.ActiveTripsDataResponse
import com.kobo.mobile_map_core.mobile_map_core.AppConfig
import com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model.AssetClassResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.AutoCompleteResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.AvailableTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.DedicatedTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview.OverviewResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.place_id.PlacesResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.reverse_geocode.ReverseGeocodeResponse
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Single

class ApiServiceImpl(private val token: String?) : ApiService {
    
    
    
    
    
    override fun fetchPlacesByAutoComplete(searchTerms: String): Single<AutoCompleteResponse> {
        return Rx2AndroidNetworking.get(AppConfig.autoCompleteUrl(searchTerms))
                .addHeaders({"Authorization" to token})
                .build()
                .getObjectSingle(AutoCompleteResponse::class.java)
    }

    override fun fetchActiveTrips(userTypeAndId: String, filterBy: String?): Single<ActiveTripsDataResponse> {
        return Rx2AndroidNetworking.get(AppConfig.activeTripsUrl(userTypeAndId, filterBy))
                .addHeaders({"Authorization" to token})
                .build()
                .getObjectSingle(ActiveTripsDataResponse::class.java)
    }

    override fun fetchDedicatedTruck(userTypeAndId: String): Single<DedicatedTruckResponse> {
        return Rx2AndroidNetworking.get(AppConfig.dedicatedTruckUrl(userTypeAndId))
                .addHeaders({"Authorization" to token})
                .build()
                .getObjectSingle(DedicatedTruckResponse::class.java)
    }

    override fun fetchGroupedAssetClass(): Single<AssetClassResponse> {
        return Rx2AndroidNetworking.get(AppConfig.groupedAssetClass())
                .addHeaders({"Authorization" to token})
                .build()
                .getObjectSingle(AssetClassResponse::class.java)
    }

    override fun getReverseGeocode(lat: Double, lng: Double): Single<ReverseGeocodeResponse> {
        return Rx2AndroidNetworking.get(AppConfig.reverseGeocode(lat, lng))
                .addHeaders({"Authorization" to token})
                .build()
                .getObjectSingle(ReverseGeocodeResponse::class.java)
    }


    override fun fetchAvailableTrucks(origin: LatLng?, destination: LatLng?, radius: Int?, assetId: String): Single<AvailableTruckResponse> {
        return Rx2AndroidNetworking.get(AppConfig.availableTrucks(origin
                , destination, radius, assetId))
                .addHeaders({"Authorization" to token})
                .build()
                .getObjectSingle(AvailableTruckResponse::class.java)
    }

    override fun fetchLatLngFromPlacesId(placesId: String): Single<PlacesResponse> {
        return Rx2AndroidNetworking.get(AppConfig.placeId(placesId))
                .addHeaders({"Authorization" to token})
                .build()
                .getObjectSingle(PlacesResponse::class.java)
    }

    override fun fetchLocationOverview(userTypeAndId: String, latLng: LatLng): Single<OverviewResponse> {
        return Rx2AndroidNetworking.get(AppConfig.locationOverview(userTypeAndId, latLng))
                .addHeaders({"Authorization" to token})
                .build()
                .getObjectSingle(OverviewResponse::class.java)
    }

}