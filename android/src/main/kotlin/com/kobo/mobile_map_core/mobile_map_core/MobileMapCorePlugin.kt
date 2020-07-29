package com.kobo.mobile_map_core.mobile_map_core

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.preference.PreferenceManager.*
import com.kobo.mobile_map_core.mobile_map_core.data.models.NavigationData
import com.kobo.mobile_map_core.mobile_map_core.ui.map.BattlefieldLandingActivity
import com.kobo.mobile_map_core.mobile_map_core.ui.map.NavigationActivity
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class MobileMapCorePlugin : MethodCallHandler {

    companion object {
        val NAVIGATION_DATA = "NAVIGATION_DATA"


        var KEY_USER_TYPE_AND_ID: String? = "user_type_and_id"
        var KEY_APP_TYPE: String? = "app_type"
        var KEY_AUTH_TOKEN: String? = "token"
        var KEY_ID: String? = "id"
        var KEY_DESTINATION: String? = "destination"
        var KEY_USER_FIRST_NAME: String? = "user_name"
        val KEY_TRUCK_REG_NUMBER = "regNumber"
        val KEY_TRIP_ID = "tripId"
        val KEY_READABLE_TRIP_ID = "tripReadId"

        private var reg: Registrar? = null

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            reg = registrar
            val channel = MethodChannel(registrar.messenger(), "mobile_map_core")
            channel.setMethodCallHandler(MobileMapCorePlugin())
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "launch_map" -> {
                val intent = Intent(getActiveContext(), BattlefieldLandingActivity::class.java)


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


                getActiveContext()?.let {
                    getDefaultSharedPreferences(it)
                            .edit()
                            .putString(KEY_USER_TYPE_AND_ID, userTypeAndId)
                            .putString(KEY_USER_FIRST_NAME, userFirstName)
                            .putString(KEY_APP_TYPE, appType)
                            .putString(KEY_AUTH_TOKEN, "Bearer $authToken")
                            .putInt(KEY_ID, id)
                            .apply()

                    when (appType) {
                        "driver" -> {
                            val navigationActivity = Intent(getActiveContext(), NavigationActivity::class.java)
                            val navigationData = NavigationData(regNumber = truckRegNumber, tripId = tripId, tripReadId = tripReadId, destination = destination)
                            navigationActivity.putExtra(NavigationActivity.NAVIGATION_DATA, navigationData)
                            startActivity(it, navigationActivity, null)
                        }
                        else -> {
                            startActivity(it, intent, null)
                        }
                    }


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


                getActiveContext()?.let {
                    getDefaultSharedPreferences(it)
                            .edit()
                            .putString(KEY_USER_TYPE_AND_ID, userTypeAndId)
                            .putString(KEY_USER_FIRST_NAME, userFirstName)
                            .putString(KEY_APP_TYPE, appType)
                            .putString(KEY_AUTH_TOKEN, "Bearer $authToken")
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
