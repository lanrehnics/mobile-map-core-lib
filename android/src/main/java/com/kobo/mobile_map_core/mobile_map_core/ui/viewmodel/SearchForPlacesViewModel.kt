package com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.AutoCompleteResponse
import com.kobo.mobile_map_core.mobile_map_core.data.repository.MainRepository
import com.kobo.mobile_map_core.mobile_map_core.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchForPlacesViewModel(private val mainRepository: MainRepository) : ViewModel() {

    //    private val autoSuggestedPlaces = MutableLiveData<Resource<List<ActiveTrip>>>()
    private val autoSuggestedPlaces = MutableLiveData<Resource<AutoCompleteResponse>>()
    private val compositeDisposable = CompositeDisposable()


    private fun fetchAutoCompletePlaces(searchTerms: String) {
        autoSuggestedPlaces.postValue(Resource.loading(null))
        compositeDisposable.add(
                mainRepository.fetchPlacesByAutoComplete(searchTerms)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ places ->
                            autoSuggestedPlaces.postValue(Resource.success(places))
                        }, { throwable ->
                            autoSuggestedPlaces.postValue(Resource.error("Something Went Wrong", null))
                        })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getPlaces(searchTerms: String): LiveData<Resource<AutoCompleteResponse>> {
        fetchAutoCompletePlaces(searchTerms)
        return autoSuggestedPlaces
    }


}