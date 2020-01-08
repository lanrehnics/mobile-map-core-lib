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
        var KEY_URL: String? = "url"
        var KEY_APP_TYPE: String? = "appType"
        var KEY_AUTH_TOKEN: String? = "authToken"
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
        if (call.method == "prepareMap") {
            val intent = Intent(getActiveContext(), MapsActivity::class.java)

            val args = call.arguments() as? ArrayList<*>
            val url = args!![0] as String
            val appType = args[1] as String
            val authToken = args[2] as String
            val id = args[3] as String

            getActiveContext()?.let {
                PreferenceManager.getDefaultSharedPreferences(it)
                        .edit()
                        .putString(KEY_URL, url)
                        .putString(KEY_APP_TYPE, appType)
                        .putString(KEY_AUTH_TOKEN, "Bearer $authToken")
                        .putString(KEY_ID, id)
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
