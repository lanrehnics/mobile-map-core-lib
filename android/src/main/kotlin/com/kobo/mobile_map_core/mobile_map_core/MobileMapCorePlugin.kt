package com.kobo.mobile_map_core.mobile_map_core

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.kobo.mobile_map_core.mobile_map_core.map.MapsActivity
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class MobileMapCorePlugin : MethodCallHandler {

    companion object {
        var KEY_CUSTOMER_STATION_URL: String? = "customer_stations_url"
        var KEY_AUTH_TOKEN: String? = "auth_token"
        var KEY_CUSTOMER_ID: String? = "customer_id"

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
            getActiveContext()?.let { startActivity(it, intent, null) }
            result.success("done")

        } else {
            result.notImplemented()
        }
    }

    private fun getActiveContext(): Context? {
        return if (reg?.activity() != null) reg?.activity() else reg?.context()
    }

}
