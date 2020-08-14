package com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel

import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.ActiveTripsDataResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kobo.mobile_map_core.mobile_map_core.data.repository.MainRepository
import com.kobo.mobile_map_core.mobile_map_core.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ActiveTripsViewModel(private val mainRepository: MainRepository) : ViewModel() {

    //    private val autoSuggestedPlaces = MutableLiveData<Resource<List<ActiveTrip>>>()
    private val listOfActiveTrips = MutableLiveData<Resource<ActiveTripsDataResponse>>()
    private val compositeDisposable = CompositeDisposable()

    init {
//        fetchActiveTrips("apapa")
    }


    fun fetchActiveTrips(userTypeAndId: String, filterBy: String? = null) {
        listOfActiveTrips.postValue(Resource.loading(null))
        compositeDisposable.add(
                mainRepository.fetchActiveTrips(userTypeAndId,filterBy)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ activeTrips ->
                            listOfActiveTrips.postValue(Resource.success(activeTrips))
                        }, { throwable ->
                            listOfActiveTrips.postValue(Resource.error("Something Went Wrong", null))
                        })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getActiveTripsData(): LiveData<Resource<ActiveTripsDataResponse>> {
        return listOfActiveTrips
    }


}

