package com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.DedicatedTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.repository.MainRepository
import com.kobo.mobile_map_core.mobile_map_core.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DedicatedTruckViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val dedicatedTruckResponse = MutableLiveData<Resource<DedicatedTruckResponse>>()
    private val compositeDisposable = CompositeDisposable()


    fun fetchDedicatedTruck(userTypeAndId: String, filterBy: String? = null) {
        dedicatedTruckResponse.postValue(Resource.loading(null))
        compositeDisposable.add(
                mainRepository.fetchDedicatedTruck()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ dedicatedTrucks ->
                            dedicatedTruckResponse.postValue(Resource.success(dedicatedTrucks))
                        }, { throwable ->
                            dedicatedTruckResponse.postValue(Resource.error("Something Went Wrong", null))
                        })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getDedicatedTruckData(): LiveData<Resource<DedicatedTruckResponse>> {
        return dedicatedTruckResponse
    }


}

