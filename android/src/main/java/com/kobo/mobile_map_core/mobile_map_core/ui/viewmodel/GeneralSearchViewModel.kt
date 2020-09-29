package com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kobo.mobile_map_core.mobile_map_core.data.models.gsearch.GeneralSearchResponse
import com.kobo.mobile_map_core.mobile_map_core.data.repository.MainRepository
import com.kobo.mobile_map_core.mobile_map_core.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class GeneralSearchViewModel(private val mainRepository: MainRepository) : ViewModel() {

    //    private val autoSuggestedPlaces = MutableLiveData<Resource<List<ActiveTrip>>>()
    private val genSearch = MutableLiveData<Resource<GeneralSearchResponse>>()
    private val compositeDisposable = CompositeDisposable()


    private fun searchForDataHere(query: MutableMap<String, String?>) {
        genSearch.postValue(Resource.loading(null))
        compositeDisposable.add(
                mainRepository.searchForData(query)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ searches ->
                            genSearch.postValue(Resource.success(searches))
                        }, { throwable ->
                            genSearch.postValue(Resource.error("Something Went Wrong", null))
                        })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun searchForData(query: MutableMap<String, String?>): LiveData<Resource<GeneralSearchResponse>> {
        searchForDataHere(query)
        return genSearch
    }


}