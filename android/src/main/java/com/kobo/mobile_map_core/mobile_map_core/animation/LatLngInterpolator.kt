package com.kobo.mobile_map_core.mobile_map_core.animation

import com.google.android.gms.maps.model.LatLng
import java.lang.Math.*
import kotlin.math.cos
import kotlin.math.sin

interface LatLngInterpolator {

    fun interpolate(fraction: Float, a: LatLng?, b: LatLng?): LatLng

    class Spherical : LatLngInterpolator {

        override fun interpolate(fraction: Float, from: LatLng?, to: LatLng?): LatLng {
            // http://en.wikipedia.org/wiki/Slerp
            val fromLat = from?.latitude?.let { toRadians(it) }
            val fromLng = from?.longitude?.let { toRadians(it) }
            val toLat = to?.latitude?.let { toRadians(it) }
            val toLng = to?.longitude?.let { toRadians(it) }
            val cosFromLat = fromLat?.let { cos(it) }
            val cosToLat = toLat?.let { cos(it) }

            // Computes Spherical interpolation coefficients.
            val angle = fromLng?.let { fromLat?.let { it1 -> toLat?.let { it2 ->
                toLng?.let { it3 ->
                    computeAngleBetween(it1, it,
                        it2, it3
                    )
                }
            } } }
            val sinAngle = angle?.let { sin(it) }
            if (sinAngle != null) {
                if (sinAngle < 1E-6) {
                    return from
                }
            }
            val a = sin((1 - fraction) * angle!!) / sinAngle!!
            val b = sin(fraction * angle) / sinAngle

            // Converts from polar to vector and interpolate.
            val x = a * cosFromLat!! * cos(fromLng) + b * cosToLat!! * toLng?.let { cos(it) }!!
            val y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng)
            val z = a * sin(fromLat) + b * sin(toLat)

            // Converts interpolated vector back to polar.
            val lat = atan2(z, sqrt(x * x + y * y))
            val lng = atan2(y, x)
            return LatLng(toDegrees(lat), toDegrees(lng))
        }

        private fun computeAngleBetween(
            fromLat: Double,
            fromLng: Double,
            toLat: Double,
            toLng: Double
        ): Double {
            // Haversine's formula
            val dLat = fromLat - toLat
            val dLng = fromLng - toLng
            return 2 * asin(
                sqrt(
                    pow(
                        sin(dLat / 2),
                        2.0
                    ) + cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2.0)
                )
            )
        }
    }
}