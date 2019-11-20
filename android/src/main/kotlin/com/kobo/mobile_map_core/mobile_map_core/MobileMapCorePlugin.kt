package com.kobo.mobile_map_core.mobile_map_core

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class MobileMapCorePlugin : MethodCallHandler {


    companion object {

        private var reg: Registrar? = null

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            reg = registrar
            val channel = MethodChannel(registrar.messenger(), "mobile_map_core")
            channel.setMethodCallHandler(MobileMapCorePlugin())
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "getPlatformVersion") {
            val intent = Intent(getActiveContext(), MapActivity::class.java)
            getActiveContext()?.let { startActivity(it, intent, null) }
            result.success("Android ${android.os.Build.VERSION.RELEASE}")

        } else {
            result.notImplemented()
        }
    }

    private fun getActiveContext(): Context? {
        return if (reg?.activity() != null) reg?.activity() else reg?.context()
    }
}
