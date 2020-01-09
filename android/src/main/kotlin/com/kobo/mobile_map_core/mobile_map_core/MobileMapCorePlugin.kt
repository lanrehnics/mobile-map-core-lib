package com.kobo.mobile_map_core.mobile_map_core

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import androidx.core.content.ContextCompat.startActivity
import com.kobo.mobile_map_core.mobile_map_core.map.MapsActivity
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class MobileMapCorePlugin : MethodCallHandler {

    companion object {

        var KEY_KOBO_CUSTOMER_URL: String? = "customer_locations_url"
        var KEY_KOBO_STATIONS_URL: String? = "kobo_stations_url"
        var KEY_APP_TYPE: String? = "app_type"
        var KEY_AUTH_TOKEN: String? = "token"
        var KEY_ID: String? = "id"

        private var reg: Registrar? = null

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            reg = registrar
            val channel = MethodChannel(registrar.messenger(), "mobile_map_core")
            channel.setMethodCallHandler(MobileMapCorePlugin())
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "launch_map") {
            val intent = Intent(getActiveContext(), MapsActivity::class.java)

            val args = call.arguments() as? Map<String, Any>
            val koboCustomerUrl = args!![KEY_KOBO_CUSTOMER_URL] as String
            val koboStationsUrl = args[KEY_KOBO_STATIONS_URL] as String
            val appType = args[KEY_APP_TYPE] as String
            val authToken = args[KEY_AUTH_TOKEN] as String
            val id = args[KEY_ID] as Int

            getActiveContext()?.let {
                PreferenceManager.getDefaultSharedPreferences(it)
                        .edit()
                        .putString(KEY_KOBO_CUSTOMER_URL, koboCustomerUrl)
                        .putString(KEY_KOBO_STATIONS_URL, koboStationsUrl)
                        .putString(KEY_APP_TYPE, appType)
                        .putString(KEY_AUTH_TOKEN, "Bearer $authToken")
                        .putInt(KEY_ID, id)
                        .apply()

                startActivity(it, intent, null)
            }
            result.success("done")

        } else {
            result.notImplemented()
        }
    }

    private fun getActiveContext(): Context? {
        return if (reg?.activity() != null) reg?.activity() else reg?.context()
    }

}
