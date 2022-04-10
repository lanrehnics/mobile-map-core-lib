package com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.libraries.maps.model.LatLng
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.DedicatedTruckResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview.OverviewResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.notifications.NotificationsResponse
import com.kobo.mobile_map_core.mobile_map_core.data.repository.MainRepository
import com.kobo.mobile_map_core.mobile_map_core.utils.Resource
import com.kobo.mobile_map_core.mobile_map_core.utils.tweakEvent.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class NotificationsViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val notificationsResponse = SingleLiveEvent<Resource<NotificationsResponse>>()
    private val compositeDisposable = CompositeDisposable()


    fun fetchNotifications() {
        notificationsResponse.postValue(Resource.loading(null))
        compositeDisposable.add(
                mainRepository.fetchNotifications()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ notiRes ->
                            notificationsResponse.postValue(Resource.success(notiRes))
                        }, { throwable ->
                            notificationsResponse.postValue(Resource.error("Something Went Wrong", null))
                        })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getNotifications(): LiveData<Resource<NotificationsResponse>> {
        return notificationsResponse
    }

}

