package com.kobo.mobile_map_core.mobile_map_core.animation

import android.os.Handler
import android.os.SystemClock
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.os.postDelayed
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine


class MarkerAnimation {

    companion object {
        fun animateMarkerToGB(
                marker: Marker?,
                finalPosition: LatLng?,
                bearing: String,
                latLngInterpolator: LatLngInterpolator
        ) {
            val startPosition = marker?.position
            val handler = Handler()
            val start = SystemClock.uptimeMillis()
            val interpolator = AccelerateDecelerateInterpolator()
            val durationInMs = 1800f

            handler.post(object : Runnable {
                var elapsed: Long = 0
                var t: Float = 0.toFloat()
                var v: Float = 0.toFloat()

                override fun run() {
                    marker?.rotation = bearing.toFloat()
                    // Calculate progress using interpolator
                    elapsed = SystemClock.uptimeMillis() - start
                    t = elapsed / durationInMs
                    v = interpolator.getInterpolation(t)

                    marker?.position = latLngInterpolator.interpolate(v, startPosition, finalPosition)

                    // Repeat till progress is complete.
                    if (t < 1) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16)
                    }
                }
            })
        }


        suspend fun animateMarker(marker: Marker?,
                                  finalPosition: LatLng?,
                                  bearing: String,
                                  latLngInterpolator: LatLngInterpolator): LatLng? {
            withContext(Dispatchers.Main) {

                val startPosition = marker?.position
                val start = SystemClock.uptimeMillis()
                val interpolator = AccelerateDecelerateInterpolator()
                val durationInMs = 1800f

                suspendCoroutine<Unit> { continuation ->
                    var elapsed: Long = 0
                    var t: Float = 0.toFloat()
                    var v: Float = 0.toFloat()

                    marker?.rotation = bearing.toFloat()
                    // Calculate progress using interpolator
                    elapsed = SystemClock.uptimeMillis() - start
                    t = elapsed / durationInMs
                    v = interpolator.getInterpolation(t)

                    marker?.position = latLngInterpolator.interpolate(v, startPosition, finalPosition)

                    if (t < 1) {
                        Thread.sleep(16)
                    }

                }
            }
            return finalPosition
        }
    }
}