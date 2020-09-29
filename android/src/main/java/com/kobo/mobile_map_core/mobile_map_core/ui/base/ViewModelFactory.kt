package com.kobo.mobile_map_core.mobile_map_core.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiHelper
import com.kobo.mobile_map_core.mobile_map_core.data.repository.MainRepository
import com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel.*

class ViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SearchForPlacesViewModel::class.java) -> {
                SearchForPlacesViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(ActiveTripsViewModel::class.java) -> {
                ActiveTripsViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(DedicatedTruckViewModel::class.java) -> {
                DedicatedTruckViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(MapLandingViewModel::class.java) -> {
                MapLandingViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(GeneralSearchViewModel::class.java) -> {
                GeneralSearchViewModel(MainRepository(apiHelper)) as T
            }
            else -> throw IllegalArgumentException("Unknown class name")
        }
    }


}