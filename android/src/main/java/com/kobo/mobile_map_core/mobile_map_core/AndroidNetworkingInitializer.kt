package com.kobo.mobile_map_core.mobile_map_core

import android.content.Context
import androidx.startup.Initializer
import com.androidnetworking.AndroidNetworking
import kotlin.collections.emptyList

class AndroidNetworkingInitializer: Initializer<Unit> {
    override fun create(context: Context) {
       return  AndroidNetworking.initialize(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return emptyList<Class<out Initializer<*>>>().toMutableList()
    }

}