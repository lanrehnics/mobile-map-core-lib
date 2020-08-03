package com.kobo.mobile_map_core.mobile_map_core

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.androidnetworking.AndroidNetworking
import com.kobo.mobile_map_core.mobile_map_core.data.models.NavigationData
import com.kobo.mobile_map_core.mobile_map_core.ui.map.BattlefieldLandingActivity
import com.kobo.mobile_map_core.mobile_map_core.ui.map.NavigationActivity
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.util.*

class MobileMapCorePlugin : MethodCallHandler {

    companion object {

        const val STAGING = "staging"
        const val PRODUCTION = "production"


        const val NAVIGATION_DATA = "NAVIGATION_DATA"


        const val KEY_USER_TYPE_AND_ID = "user_type_and_id"
        const val KEY_APP_TYPE = "app_type"
        const val KEY_AUTH_TOKEN = "token"
        const val KEY_ID = "id"
        const val KEY_DESTINATION = "destination"
        const val KEY_USER_FIRST_NAME = "user_name"
        const val KEY_TRUCK_REG_NUMBER = "reg_number"
        const val KEY_TRIP_ID = "trip_id"
        const val KEY_READABLE_TRIP_ID = "trip_read_id"
        const val KEY_GEO_BASE_URL = "geo_base_url"
        const val KEY_KOBO_BASE_URL = "kobo_base_url"
        const val KEY_ENVIRONMENT = "environment"
        const val KEY_SIMULATE_ROUTE_FOR_DRIVER = "simulate_route_for_driver"

        lateinit var CLOUDMQTT_HOST: String
        lateinit var CLOUDMQTT_USER: String
        lateinit var CLOUDMQTT_PASS: String

        private val DEV_CLOUDMQTT_HOST = "tcp://smart-journalist.cloudmqtt.com:1883"
        private val DEV_CLOUDMQTT_USER = "mobileand"
        private val DEV_CLOUDMQTT_PASS = "kbMob20@ge"


        private val PROD_CLOUDMQTT_HOST = "tcp://cool-newsreader.cloudmqtt.com:1883"
        private val PROD_CLOUDMQTT_USER = "geo-android"
        private val PROD_CLOUDMQTT_PASS = "12Jayi$$3KbndG@e"


        const val KEY_CLOUDMQTT_HOST = "key_cloudmqtt_host"
        const val KEY_CLOUDMQTT_USER = "key_cloudmqtt_user"
        const val KEY_CLOUDMQTT_PASS = "key_cloudmqtt_pass"


        private var reg: Registrar? = null

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            reg = registrar
            val channel = MethodChannel(registrar.messenger(), "mobile_map_core")
            channel.setMethodCallHandler(MobileMapCorePlugin())
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {

//        AndroidNetworking.initialize(getActiveContext())


        when (call.method) {
            "launch_map" -> {
                val intent = Intent(getActiveContext(), BattlefieldLandingActivity::class.java)


                val args = call.arguments() as? Map<String, Any?>?
                val appType = args?.get(KEY_APP_TYPE) as String?
                val userTypeAndId = args?.get(KEY_USER_TYPE_AND_ID) as String?
                val authToken = args?.get(KEY_AUTH_TOKEN) as String?
                val geoBaseUrl = args?.get(KEY_GEO_BASE_URL) as String?
                val koboBaseUrl = args?.get(KEY_KOBO_BASE_URL) as String?
                val id = args?.get(KEY_ID) as Int
                val userFirstName = args[KEY_USER_FIRST_NAME] as String?

                val environment = if (args[KEY_ENVIRONMENT] != null) {
                    args[KEY_ENVIRONMENT] as String?
                } else {
                    STAGING
                }

                val simulateRouteForDriver = if (args[KEY_SIMULATE_ROUTE_FOR_DRIVER] != null) {
                    args[KEY_SIMULATE_ROUTE_FOR_DRIVER] as Boolean
                } else {
                    false
                }


                when (environment?.toLowerCase(Locale.getDefault())) {
                    PRODUCTION -> {
                        CLOUDMQTT_HOST = PROD_CLOUDMQTT_HOST
                        CLOUDMQTT_USER = PROD_CLOUDMQTT_USER
                        CLOUDMQTT_PASS = PROD_CLOUDMQTT_PASS
                    }
                    else -> {
                        CLOUDMQTT_HOST = DEV_CLOUDMQTT_HOST
                        CLOUDMQTT_USER = DEV_CLOUDMQTT_USER
                        CLOUDMQTT_PASS = DEV_CLOUDMQTT_PASS
                    }
                }


                getActiveContext()?.let {
                    getDefaultSharedPreferences(it)
                            .edit()
                            .putString(KEY_USER_TYPE_AND_ID, userTypeAndId)
                            .putString(KEY_USER_FIRST_NAME, userFirstName)
                            .putString(KEY_APP_TYPE, appType)
                            .putString(KEY_AUTH_TOKEN, "Bearer $authToken")
                            .putString(KEY_GEO_BASE_URL, geoBaseUrl)
                            .putString(KEY_KOBO_BASE_URL, koboBaseUrl)
                            .putString(KEY_CLOUDMQTT_HOST, CLOUDMQTT_HOST)
                            .putString(KEY_CLOUDMQTT_USER, CLOUDMQTT_USER)
                            .putString(KEY_CLOUDMQTT_PASS, CLOUDMQTT_PASS)
                            .putBoolean(KEY_SIMULATE_ROUTE_FOR_DRIVER, simulateRouteForDriver)
                            .putInt(KEY_ID, id)
                            .apply()


                    startActivity(it, intent, null)

                }


                result.success("done")

            }


            "navigate_driver" -> {

                val args = call.arguments() as? Map<String, Any?>?
                val appType = args?.get(KEY_APP_TYPE) as String?
                val truckRegNumber = args?.get(KEY_TRUCK_REG_NUMBER) as String?
                val tripReadId = args?.get(KEY_READABLE_TRIP_ID) as String?
                val tripId = args?.get(KEY_TRIP_ID) as String?
                val destination = args?.get(KEY_DESTINATION) as List<Double>?
                val userTypeAndId = args?.get(KEY_USER_TYPE_AND_ID) as String?
                val authToken = args?.get(KEY_AUTH_TOKEN) as String?
                val id = args?.get(KEY_ID) as Int
                val userFirstName = args[KEY_USER_FIRST_NAME] as String?

                val simulateRouteForDriver = if (args[KEY_SIMULATE_ROUTE_FOR_DRIVER] != null) {
                    args[KEY_SIMULATE_ROUTE_FOR_DRIVER] as Boolean
                } else {
                    false
                }


                val environment = args[KEY_ENVIRONMENT] as String?

                when (environment?.toLowerCase(Locale.getDefault())) {
                    PRODUCTION -> {
                        CLOUDMQTT_HOST = PROD_CLOUDMQTT_HOST
                        CLOUDMQTT_USER = PROD_CLOUDMQTT_USER
                        CLOUDMQTT_PASS = PROD_CLOUDMQTT_PASS
                    }
                    else -> {
                        CLOUDMQTT_HOST = DEV_CLOUDMQTT_HOST
                        CLOUDMQTT_USER = DEV_CLOUDMQTT_USER
                        CLOUDMQTT_PASS = DEV_CLOUDMQTT_PASS
                    }
                }



                getActiveContext()?.let {
                    getDefaultSharedPreferences(it)
                            .edit()
                            .putString(KEY_USER_TYPE_AND_ID, userTypeAndId)
                            .putString(KEY_USER_FIRST_NAME, userFirstName)
                            .putString(KEY_APP_TYPE, appType)
                            .putString(KEY_AUTH_TOKEN, "Bearer $authToken")
                            .putString(KEY_CLOUDMQTT_HOST, CLOUDMQTT_HOST)
                            .putString(KEY_CLOUDMQTT_USER, CLOUDMQTT_USER)
                            .putString(KEY_CLOUDMQTT_PASS, CLOUDMQTT_PASS)
                            .putBoolean(KEY_SIMULATE_ROUTE_FOR_DRIVER, simulateRouteForDriver)
                            .putInt(KEY_ID, id)
                            .apply()


                    val navigationActivity = Intent(getActiveContext(), NavigationActivity::class.java)
                    val navigationData = NavigationData(regNumber = truckRegNumber, tripId = tripId, tripReadId = tripReadId, destination = destination)
                    navigationActivity.putExtra(NavigationActivity.NAVIGATION_DATA, navigationData)
                    startActivity(it, navigationActivity, null)

                }


                result.success("done")

            }
            else -> {
                result.notImplemented()
            }
        }
    }

    private fun getActiveContext(): Context? {
        return if (reg?.activity() != null) reg?.activity() else reg?.context()
    }

}
