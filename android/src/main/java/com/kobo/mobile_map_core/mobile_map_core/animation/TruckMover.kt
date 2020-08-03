package com.kobo.mobile_map_core.mobile_map_core.animation

import android.content.Context
import android.graphics.Color
import android.os.Handler
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.LatLngBounds
import com.google.android.libraries.maps.model.Marker
import com.google.android.libraries.maps.model.PolylineOptions
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Location
import com.kobo.mobile_map_core.mobile_map_core.ui.map.NewBaseMapActivity
import java.util.*
import kotlin.collections.ArrayList

class TruckMover(private val googleMap: GoogleMap, private val context: Context, private var selectedMarker: Marker?) {

    //    private var originMarker: Marker? = null
//    private var destinationMarker: Marker? = null
//    private var grayPolyline: Polyline? = null
//    private var blackPolyline: Polyline? = null
    private var movingTruckMarker: Marker? = null
    private var previousLatLng: LatLng? = null
    private var currentLatLng: LatLng? = null

    private var oldLocationList: Int = 0
    private var newLocationList: Int = 0

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var locationList: MutableList<Location?> = arrayListOf()

//    private lateinit var timer: Timer


    init {

//        timer = Timer()
//        timer.scheduleAtFixedRate(object : TimerTask() {
//            override fun run() {
//                println("Yes")
//            }
//        },
//                0, 1000)


//        handler = Handler()
//        runnable = Runnable {
//            run {
//                if (locationListIterator.hasNext()) {
//                    locationListIterator.next()?.let { updateTruckLocation(it) }
//                    handler.postDelayed(runnable, 3000)
//                }
//            }
//        }
//        handler.postDelayed(runnable, 5000)


    }


    fun startTruckMovementTimer() {
        var index = 0
        handler = Handler()
        runnable = Runnable {
            run {
                if ((newLocationList > oldLocationList) && index < locationList.size) {
                    locationList[index]?.let { updateTruckLocation(it) }
                    ++index

                    handler.postDelayed(runnable, 3000)
                }else{
                    handler.postDelayed(runnable, 3000)
                }

            }
        }
        handler.postDelayed(runnable, 5000)

//        timer = Timer()
//        timer.scheduleAtFixedRate(object : TimerTask() {
//            override fun run() {
//                println("Yes")
//            }
//        },
//                0, 1000)


    }

    fun stopTruckMovementTimer() {
        handler.removeCallbacks(runnable)
//        timer.cancel()
    }


    private fun moveCamera(latLng: LatLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun animateCamera(latLng: LatLng, subList: MutableList<LatLng>) {

        val boundsBuilder = LatLngBounds.Builder()


        subList.forEach { item ->
            boundsBuilder.include(item)
        }

        val bounds = boundsBuilder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, 200)

        googleMap.animateCamera(cu)
    }


    private fun animateCamera(latLng: LatLng) {
//        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5f).build()
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

//        googleMap.uiSettings.isRotateGesturesEnabled = true

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
    }


    fun addMoreLocation(locations: List<Location?>) {
        //Note this arrangement id very important
        oldLocationList = locationList.size
        locationList.addAll(locations)
        newLocationList = locationList.size + locations.size


    }

    private fun animateCamera(
            currentLatLng: LatLng,
            previousLatLng: LatLng,
            nextLatLng: LatLng
    ) {
        val boundsBuilder = LatLngBounds.Builder()
        boundsBuilder.include(previousLatLng)
        boundsBuilder.include(currentLatLng)
        boundsBuilder.include(nextLatLng)

        val bounds = boundsBuilder.build()

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, 200)
        googleMap.animateCamera(cu)
    }


//    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker {
//        val bitmapDescriptor =
//                BitmapDescriptorFactory.fromBitmap(MapUtils.getOriginDestinationMarkerBitmap())
//        return googleMap.addMarker(
//                MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
//        )
//    }

    fun showDefaultLocationOnMap(latLng: LatLng) {
        moveCamera(latLng)
        animateCamera(latLng)
    }

    /**
     * This function is used to draw the path between the Origin and Destination.
     */
    private fun showPath(latLngList: ArrayList<LatLng>) {
        val builder = LatLngBounds.Builder()
        for (latLng in latLngList) {
            builder.include(latLng)
        }
        val bounds = builder.build()
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))

        val polylineOptions = PolylineOptions()
        polylineOptions.color(Color.GRAY)
        polylineOptions.width(5f)
        polylineOptions.addAll(latLngList)
//        grayPolyline = googleMap.addPolyline(polylineOptions)

        val blackPolylineOptions = PolylineOptions()
        blackPolylineOptions.color(Color.BLACK)
        blackPolylineOptions.width(5f)
//        blackPolyline = googleMap.addPolyline(blackPolylineOptions)

//        originMarker = addOriginDestinationMarkerAndGet(latLngList[0])
//        originMarker?.setAnchor(0.5f, 0.5f)
//        destinationMarker = addOriginDestinationMarkerAndGet(latLngList[latLngList.size - 1])
//        destinationMarker?.setAnchor(0.5f, 0.5f)

//        val polylineAnimator = AnimationUtils.polylineAnimator()
//        polylineAnimator.addUpdateListener { valueAnimator ->
//            val percentValue = (valueAnimator.animatedValue as Int)
//            val index = (grayPolyline?.points!!.size) * (percentValue / 100.0f).toInt()
//            blackPolyline?.points = grayPolyline?.points!!.subList(0, index)
//        }
//        polylineAnimator.start()
    }

    /**
     * This function is used to update the location of the truck while moving from Origin to Destination
     */
    private fun updateTruckLocation(locaction: Location) {
        if (movingTruckMarker == null) {
            movingTruckMarker = selectedMarker
        }
        if (previousLatLng == null) {
            currentLatLng = NewBaseMapActivity.toLatLngNotNull(locaction.coordinates)
            previousLatLng = currentLatLng
            movingTruckMarker?.position = currentLatLng
            movingTruckMarker?.setAnchor(0.5f, 0.5f)
            animateCamera(currentLatLng!!)
        } else {
            previousLatLng = currentLatLng
            currentLatLng = NewBaseMapActivity.toLatLngNotNull(locaction.coordinates)
            val valueAnimator = AnimationUtils.truckAnimator()
            valueAnimator.addUpdateListener { va ->
                if (currentLatLng != null && previousLatLng != null) {
                    val multiplier = va.animatedFraction
                    val nextLocation = LatLng(
                            multiplier * currentLatLng!!.latitude + (1 - multiplier) * previousLatLng!!.latitude,
                            multiplier * currentLatLng!!.longitude + (1 - multiplier) * previousLatLng!!.longitude
                    )
                    movingTruckMarker?.position = nextLocation
                    val rotation = MapUtils.getRotation(previousLatLng!!, nextLocation)
//                    if (!rotation.isNaN()) {
//                        movingCabMarker?.rotation = rotation
//                    }
                    movingTruckMarker?.rotation = locaction.bearing.toFloat()
                    movingTruckMarker?.setAnchor(0.5f, 0.5f)

//                    animateCamera(previousLatLng!!, currentLatLng!!, nextLocation)
                    animateCamera(nextLocation)
                }
            }
            valueAnimator.start()
        }
    }

//    fun showMovingTruck() {
//        handler = Handler()
//        runnable = Runnable {
//            run {
//                if (locationListIterator.hasNext()) {
//                    locationListIterator.next()?.let { updateTruckLocation(it) }
//                    handler.postDelayed(runnable, 3000)
//                }
//            }
//        }
//        handler.postDelayed(runnable, 5000)
//    }

}