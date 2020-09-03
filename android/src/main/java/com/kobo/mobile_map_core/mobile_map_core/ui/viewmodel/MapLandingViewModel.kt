package com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.libraries.maps.model.LatLng
import com.kobo.mobile_map_core.mobile_map_core.data.models.BookTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model.AssetClassResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.AvailableTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview.OverviewResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.orders.AvailableOrdersResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.place_id.PlacesResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.reverse_geocode.ReverseGeocodeResponse
import com.kobo.mobile_map_core.mobile_map_core.data.repository.MainRepository
import com.kobo.mobile_map_core.mobile_map_core.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MapLandingViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val groupedAssetClass = MutableLiveData<Resource<AssetClassResponse>>()
    private val reverseGeoCode = MutableLiveData<Resource<ReverseGeocodeResponse>>()
    private val availableTrucks = MutableLiveData<Resource<AvailableTruckResponse>>()
    private val availableOrders = MutableLiveData<Resource<AvailableOrdersResponse>>()
    private val latLngFromPlacesId = MutableLiveData<Resource<PlacesResponse>>()
    private val locationOverview = MutableLiveData<Resource<OverviewResponse>>()
    private val bookTruckResponse = MutableLiveData<Resource<BookTruckResponse>>()
    private val compositeDisposable = CompositeDisposable()

    init {
        fetchGroupedAsset()
    }


    private fun fetchGroupedAsset() {
        groupedAssetClass.postValue(Resource.loading(null))
        compositeDisposable.add(
                mainRepository.fetchGroupedAssetClass()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ assetClass ->
                            groupedAssetClass.postValue(Resource.success(assetClass))
                        }, { throwable ->
                            groupedAssetClass.postValue(Resource.error("Something Went Wrong", null))
                        })
        )
    }

    private fun reverseGeocode(latLng: LatLng) {
        reverseGeoCode.postValue(Resource.loading(null))
        compositeDisposable.add(
                mainRepository.getReverseGeocode(lat = latLng.latitude, lng = latLng.longitude)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ reverseGeoResponse ->
                            reverseGeoCode.postValue(Resource.success(reverseGeoResponse))
                        }, { throwable ->
                            reverseGeoCode.postValue(Resource.error("Something Went Wrong", null))
                        })
        )
    }


    private fun searchAvailableTruck(origin: LatLng?, destination: LatLng?, radius: Int?, assetId: String) {
        availableTrucks.postValue(Resource.loading(null))
        compositeDisposable.add(
                mainRepository.fetchAvailableTrucks(origin, destination, radius, assetId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ available ->
                            availableTrucks.postValue(Resource.success(available))
                        }, { throwable ->
                            availableTrucks.postValue(Resource.error("Something Went Wrong", null))
                        })
        )
    }

    private fun searchAvailableOrder(origin: LatLng?, assetType: String) {
        availableOrders.postValue(Resource.loading(null))
        compositeDisposable.add(
                mainRepository.fetchAvailableOrders(origin, assetType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ available ->
                            availableOrders.postValue(Resource.success(available))
                        }, { throwable ->
                            availableOrders.postValue(Resource.error("Something Went Wrong", null))
                        })
        )
    }


    private fun fetchLatLngFromPlaceId(placeId: String) {
        latLngFromPlacesId.postValue(Resource.loading(null))
        compositeDisposable.add(
                mainRepository.placeId(placeId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ placesId ->
                            latLngFromPlacesId.postValue(Resource.success(placesId))
                        }, { throwable ->
                            latLngFromPlacesId.postValue(Resource.error("Something Went Wrong", null))
                        })
        )
    }


    private fun fetchLocationOverview(userTypeAndId: String, latLng: LatLng) {
        locationOverview.postValue(Resource.loading(null))
        compositeDisposable.add(
                mainRepository.fetchLocationOverview(userTypeAndId, latLng)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ locationOverView ->
                            locationOverview.postValue(Resource.success(locationOverView))
                        }, { throwable ->
                            locationOverview.postValue(Resource.error("Something Went Wrong", null))
                        })
        )
    }

    private fun prepareTruckBooking(truckReg: String?) {
        bookTruckResponse.postValue(Resource.loading(null))
        compositeDisposable.add(
                mainRepository.bookTruck(truckReg)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ truckBooking ->
                            bookTruckResponse.postValue(Resource.success(truckBooking))
                        }, { throwable ->
                            bookTruckResponse.postValue(Resource.error(throwable.message
                                    ?: "Something Went Wrong", null))
                        })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getGroupedAssetClass(): LiveData<Resource<AssetClassResponse>> {
        return groupedAssetClass
    }

    fun getReverseGeocode(latLng: LatLng): LiveData<Resource<ReverseGeocodeResponse>> {
        reverseGeocode(latLng)
        return reverseGeoCode
    }


    fun fetchAvailableTruck(origin: LatLng?, destination: LatLng?, radius: Int?, assetId: String): LiveData<Resource<AvailableTruckResponse>> {
        searchAvailableTruck(origin, destination, radius, assetId)
        return availableTrucks
    }

    fun fetchAvailableOrder(origin: LatLng?, assetType: String): LiveData<Resource<AvailableOrdersResponse>> {
        searchAvailableOrder(origin, assetType)
        return availableOrders
    }

    fun fetchLatLngFromPlacesId(placesId: String): LiveData<Resource<PlacesResponse>> {
        fetchLatLngFromPlaceId(placesId)
        return latLngFromPlacesId
    }

    fun getLocationOverview(placesId: String, latLng: LatLng): LiveData<Resource<OverviewResponse>> {
        fetchLocationOverview(placesId, latLng)
        return locationOverview
    }

    fun bookTruck(truckReg: String?): LiveData<Resource<BookTruckResponse>> {
        prepareTruckBooking(truckReg)
        return bookTruckResponse
    }


}

