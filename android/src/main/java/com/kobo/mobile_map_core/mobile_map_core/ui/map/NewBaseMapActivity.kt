package com.kobo.mobile_map_core.mobile_map_core.ui.map

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.*
import com.google.android.libraries.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.models.ClearCommand
import com.kobo.mobile_map_core.mobile_map_core.data.models.TruckClusterItem
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Events
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Trips
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import com.kobo.mobile_map_core.mobile_map_core.data.services.MapService
import com.kobo.mobile_map_core.mobile_map_core.enums.FocusFrom
import com.kobo.mobile_map_core.mobile_map_core.enums.MapDisplayMode
import com.kobo360.map.MarkerClusterRenderer
import com.mapbox.geojson.Point
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.util.*

abstract class NewBaseMapActivity : AppCompatActivity(), OnMapReadyCallback {


    private val CLOUDMQTT_HOST = "tcp://smart-journalist.cloudmqtt.com:1883"
    private val CLOUDMQTT_PORT = "1883"
    private val CLOUDMQTT_USER = "mobileand"
    private val CLOUDMQTT_PASS = "kbMob20@ge"

    private lateinit var mqttAndroidClient: MqttAndroidClient
    private lateinit var mqttTopic: String


    private lateinit var travelledPolylineOptions: PolylineOptions
    private lateinit var currentBestRoutePolylineOptions: PolylineOptions
    private lateinit var currentBestRoutePolyline: Polyline
    private lateinit var travelledPathPolyline: Polyline


    protected lateinit var context: Context
    protected lateinit var mapService: MapService
    protected lateinit var tripInfo: Trucks
    protected lateinit var truckInfo: Trucks
    protected lateinit var mMap: GoogleMap
    protected lateinit var mMapView: View
    protected lateinit var currentLatLng: LatLng
    protected lateinit var fusedLocationClient: FusedLocationProviderClient
    protected lateinit var clusterManager: ClusterManager<TruckClusterItem>
    protected var truckMarkerManager: MutableMap<String, Trucks>? = mutableMapOf()
    protected var tripMarkerManager: MutableMap<String, Trucks>? = mutableMapOf()
    protected var markerManager: MutableMap<String, Marker> = mutableMapOf()


    protected lateinit var displayMode: MapDisplayMode
    protected lateinit var focusFrom: FocusFrom

    protected fun initMapStuffs() {
        displayMode = MapDisplayMode.UserFocusMode
        focusFrom = FocusFrom.Default
        mapService = MapService(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }


    fun clearMapAndData(command: ClearCommand) {

        clusterManager.clearItems()
//        customerLocationList?.clear()
//        koboStationLocationsList?.clear()

        when (command) {
            ClearCommand.MAP -> {
                mMap.clear()
//                listTripStatusFilter.clear()
            }
            ClearCommand.DATA -> {
                truckMarkerManager?.clear()
                tripMarkerManager?.clear()
                markerManager.clear()
//                listFilterTerms.clear()
            }
            ClearCommand.WITHOUT_VIEW -> {
                mMap.clear()
                truckMarkerManager?.clear()
                tripMarkerManager?.clear()
                markerManager.clear()
            }
            else -> {
                mMap.clear()
                markerManager.clear()
                truckMarkerManager?.clear()
                tripMarkerManager?.clear()
//                backArrowButton.visibility = View.VISIBLE
//                btnCloseTruckInfo.visibility = View.INVISIBLE
//                listFilterTerms.clear()
            }
        }
    }


    protected fun drawPolyLine(
            travelledPolyList: List<LatLng>,
            currentBestPolyList: List<LatLng>
    ) {
        val builder = LatLngBounds.Builder()
        for (latLng in travelledPolyList) {
            builder.include(latLng)
        }
        val bounds = builder.build()
        val mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
        mMap.animateCamera(mCameraUpdate)

        travelledPolylineOptions = PolylineOptions()
        travelledPolylineOptions.color(Color.parseColor("#FF9833"))
        travelledPolylineOptions.width(10f)
        travelledPolylineOptions.startCap(SquareCap())
        travelledPolylineOptions.endCap(SquareCap())
        travelledPolylineOptions.jointType(JointType.ROUND)
        travelledPolylineOptions.addAll(travelledPolyList)
        travelledPathPolyline = mMap.addPolyline(travelledPolylineOptions)

        currentBestRoutePolylineOptions = PolylineOptions()
        currentBestRoutePolylineOptions.color(Color.BLACK)
        currentBestRoutePolylineOptions.width(10f)
        currentBestRoutePolylineOptions.startCap(SquareCap())
        currentBestRoutePolylineOptions.endCap(SquareCap())
        currentBestRoutePolylineOptions.jointType(JointType.ROUND)
        currentBestRoutePolylineOptions.addAll(currentBestPolyList)
        currentBestRoutePolyline = mMap.addPolyline(currentBestRoutePolylineOptions)


//        mMap.addMarker(
//                MarkerOptions()
//                        .position(polyLineList.get(polyLineList.size - 1))
//                        .icon(
//                                BitmapDescriptorFactory.fromResource(
//                                        R.drawable.warehouse
//                                )
//                        )
//                        .title(selectedTruck?.d?.reg_number)
//                        .snippet("Delivery up location at ${selectedTruck?.d?.deliveryStation?.address}")
//        )

    }


    protected fun drawPolyLine(
            polyList: List<LatLng>
    ) {
        travelledPolylineOptions = PolylineOptions()
        travelledPolylineOptions.color(Color.BLACK)
        travelledPolylineOptions.width(10f)
        travelledPolylineOptions.startCap(SquareCap())
        travelledPolylineOptions.endCap(SquareCap())
        travelledPolylineOptions.jointType(JointType.ROUND)
        travelledPolylineOptions.addAll(polyList)
        travelledPathPolyline = mMap.addPolyline(travelledPolylineOptions)

    }


    protected fun setUpLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), BattlefieldLandingActivity.LOCATION_PERMISSION_REQUEST_CODE)
            return
        } else {
//            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                // 3
                if (location != null) {
//                lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
//                    bootStrapPickupStationsAndTrucks()
                }
            }

//            val locationButton = (mMapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(Integer.parseInt("2"))

//            locationButton.visibility = View.INVISIBLE
//
//            val rlp=locationButton.layoutParams as (RelativeLayout.LayoutParams)
//            // position on right bottom
//            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0)
//            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
//            rlp.setMargins(0,0,60,30)
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        clusterManager = ClusterManager(this, googleMap)
        setUpClusterManagerClickListener()
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        //mMap.isTrafficEnabled = true
        mMap.isIndoorEnabled = false
        mMap.isBuildingsEnabled = true
//        mMap.uiSettings.isCompassEnabled = true
//        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style))

        mMap.uiSettings.setAllGesturesEnabled(true)

//        mMap.moveCamera(
//                CameraUpdateFactory.newCameraPosition(
//                        CameraPosition.Builder()
//                                .target(googleMap.cameraPosition.target)
//                                .zoom(15f)
////                .bearing(30f)
////                .tilt(45f)
//                                .build()
//                )
//        )
        when (displayMode) {
            MapDisplayMode.SingleTripTruckFocusMode -> tripTruckFocusMode()
            MapDisplayMode.SingleTruckFocusMode -> truckFocusMode()
            MapDisplayMode.UserFocusMode -> userFocusMode()
            MapDisplayMode.MultipleTruckClusteringMode -> multipleTruckClusteringMode()
        }
    }

    abstract fun setUpClusterManagerClickListener()

    abstract fun multipleTruckClusteringMode()

    fun userFocusMode() {
        setUpLocationPermission()
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Get last known location. In some rare situations this can be null.
            // 3
            if (location != null) {
//                lastLocation = location
                currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }

//        var circleOptions = CircleOptions()
//            .center(headOffice).radius(800.0)
//            .fillColor(Color.parseColor("#d4e8f2"))
//            .strokeColor(Color.parseColor("#d4e8f2"))
//        mMap.addCircle(circleOptions)

    }

    fun tripTruckFocusMode() {

        clearMapAndData(ClearCommand.MAP)
        if (tripInfo.tripDetail?.travelledRoutePolyline?.isNotEmpty()!! && tripInfo.tripDetail?.currentBestRoute?.isNotEmpty()!!) {
            drawPolyLine(travelledPolyList = mapService.decodePoly(tripInfo.tripDetail?.travelledRoutePolyline!!),
                    currentBestPolyList = mapService.decodePoly(tripInfo.tripDetail?.currentBestRoute!!))
        }

        val marker = mMap.addMarker(
                tripInfo.lastKnownLocation?.coordinates?.let { it1 -> toLatLng(it1) }?.let { it2 ->
                    MarkerOptions()
                            .position(it2)
                            //                                                .title(selectedTruck!!.d.reg_number)
                            .rotation(tripInfo.bearing.toFloat())
                            .icon(truckFromStatus(tripInfo))
                }
        )

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tripInfo.lastKnownLocation?.coordinates?.let { latLngFromList(it) }, 12f))

        mqttTopic = "client/track/${tripInfo.regNumber}".toLowerCase(Locale.getDefault())
        setUpMQTT()

        tripInfo.events?.let { events ->
            events.forEach { ev ->

                ev?.let { it ->
                    mMap.addMarker(
                            toLatLng(it.location.coordinates)?.let { it1 ->
                                MarkerOptions()
                                        .position(it1)
                                        //                                                .title(selectedTruck!!.d.reg_number)
                                        .icon(eventMarkerIconFromEventName(it))
                            }
                    )
                }

            }
        }


    }


    fun truckFocusMode() {

        clearMapAndData(ClearCommand.MAP)
        if (truckInfo.tripDetail?.travelledRoutePolyline?.isNotEmpty()!! && truckInfo.tripDetail?.currentBestRoute?.isNotEmpty()!!) {
            drawPolyLine(travelledPolyList = mapService.decodePoly(truckInfo.tripDetail?.travelledRoutePolyline!!),
                    currentBestPolyList = mapService.decodePoly(truckInfo.tripDetail?.currentBestRoute!!))
        }

        val marker = mMap.addMarker(
                truckInfo.lastKnownLocation?.coordinates?.let { it1 -> toLatLng(it1) }?.let { it2 ->
                    MarkerOptions()
                            .position(it2)
                            //                                                .title(selectedTruck!!.d.reg_number)
                            .rotation(truckInfo.bearing.toFloat())
                            .icon(truckFromStatus(truckInfo))
                }
        )

        truckInfo.events?.let { events ->
            events.forEach { ev ->
                ev?.let { it ->
                    mMap.addMarker(
                            toLatLng(it.location.coordinates)?.let { it1 ->
                                MarkerOptions()
                                        .position(it1)
                                        .icon(eventMarkerIconFromEventName(it))
                            }
                    )
                }

            }
        }


    }

    protected fun openMapFragment() {

        val supportMapFragment = SupportMapFragment.newInstance(GoogleMapOptions()
                .mapId(resources.getString(R.string.map_id)))
        supportFragmentManager.beginTransaction().add(R.id.mainActiveTripsHome, supportMapFragment).addToBackStack("mapFragment").commit()
        supportMapFragment.getMapAsync(this)
    }


    companion object {

        fun latLngFromList(latLngList: List<Double>): LatLng {
            return LatLng(latLngList[1], latLngList[0])
        }

        fun toLatLng(list: List<Double>?): LatLng? {
            return list?.get(1)?.let { LatLng(it, list[0]) }
        }

        fun toPoint(list: List<Double>?): Point {
            return list?.let { Point.fromLngLat(it[0], it[1]) }!!
        }

        fun toPoint(latLng: LatLng): Point {
            return Point.fromLngLat(latLng.longitude, latLng.latitude)
        }


        fun toMapBoxLatLng(latLng: LatLng): com.mapbox.mapboxsdk.geometry.LatLng {
            return com.mapbox.mapboxsdk.geometry.LatLng(latLng.longitude, latLng.latitude)
        }

        fun toMapBoxLatLng(list: List<Double>): com.mapbox.mapboxsdk.geometry.LatLng {
            return com.mapbox.mapboxsdk.geometry.LatLng(list[1], list[0])
        }

        fun toMapBoxLatLng(point: Point): com.mapbox.mapboxsdk.geometry.LatLng {
            return com.mapbox.mapboxsdk.geometry.LatLng(point.latitude(), point.longitude())
        }


        fun truckFromStatus(tp: Trucks): BitmapDescriptor? {


            return if (!tp.onTrip) {
                BitmapDescriptorFactory.fromResource(R.drawable.ic_available_truck)
            } else {
                when (tp.tripDetail?.overviewStatus.toString().toLowerCase(Locale.getDefault())) {
                    "toPickup".toLowerCase(Locale.getDefault()) -> BitmapDescriptorFactory.fromResource(R.drawable.ic_enroute_truck)
                    "toDelivery".toLowerCase(Locale.getDefault()) -> BitmapDescriptorFactory.fromResource(R.drawable.ic_active_trip_truck)
                    "atDestination".toLowerCase(Locale.getDefault()) -> BitmapDescriptorFactory.fromResource(R.drawable.ic_active_trip_truck)
                    "stopped".toLowerCase(Locale.getDefault()) -> BitmapDescriptorFactory.fromResource(R.drawable.ic_flagged_truck)
                    "diverted".toLowerCase(Locale.getDefault()) -> BitmapDescriptorFactory.fromResource(R.drawable.ic_diverted_truck)
                    else -> BitmapDescriptorFactory.fromResource(R.drawable.ic_active_trip_truck)
                }
            }


        }

        fun truckFromStatus(tp: Trips): BitmapDescriptor? {


            return if (!tp.onTrip) {
                BitmapDescriptorFactory.fromResource(R.drawable.ic_available_truck)
            } else {
                println(null.toString())
                when (tp.tripDetail?.overviewStatus?.toString()?.toLowerCase(Locale.getDefault())) {
                    "toPickup".toLowerCase(Locale.getDefault()) -> BitmapDescriptorFactory.fromResource(R.drawable.ic_enroute_truck)
                    "toDelivery".toLowerCase(Locale.getDefault()) -> BitmapDescriptorFactory.fromResource(R.drawable.ic_truck_to_delivery)
                    "atDestination".toLowerCase(Locale.getDefault()) -> BitmapDescriptorFactory.fromResource(R.drawable.ic_active_trip_truck)
                    "stopped".toLowerCase(Locale.getDefault()) -> BitmapDescriptorFactory.fromResource(R.drawable.ic_flagged_truck)
                    "diverted".toLowerCase(Locale.getDefault()) -> BitmapDescriptorFactory.fromResource(R.drawable.ic_diverted_truck)
                    else -> BitmapDescriptorFactory.fromResource(R.drawable.ic_active_trip_truck)
                }
            }


        }


        fun eventIconFromEventName(event: Events): BitmapDescriptor? {
            return BitmapDescriptorFactory.fromResource(when (event.name.trim().toLowerCase(Locale.getDefault())) {
                "SHARP TURN".toLowerCase(Locale.getDefault()) -> R.drawable.ic_overspeeding
                "OVERSPEEDING".toLowerCase(Locale.getDefault()) -> R.drawable.ic_overspeeding
                "TRIP START".toLowerCase(Locale.getDefault()) -> R.drawable.ic_trip_started
                "TRIP END".toLowerCase(Locale.getDefault()) -> R.drawable.ic_trip_end
                "TRUCK BREAKDOWN".toLowerCase(Locale.getDefault()) -> R.drawable.ic_truck_breakdown
                "POLICE STOP".toLowerCase(Locale.getDefault()) -> R.drawable.ic_truck_breakdown
                "DROP OFF DELIVERY".toLowerCase(Locale.getDefault()) -> R.drawable.ic_drop_off_delivery
                else -> R.drawable.ic_overspeeding

            }
            )


        }


        fun eventMarkerIconFromEventName(event: Events): BitmapDescriptor? {
            return BitmapDescriptorFactory.fromResource(when (event.name.trim().toLowerCase(Locale.getDefault())) {
                "SHARP TURN".toLowerCase(Locale.getDefault()) -> R.drawable.ic_marker_overspeeding
                "OVERSPEEDING".toLowerCase(Locale.getDefault()) -> R.drawable.ic_marker_overspeeding
                "TRIP START".toLowerCase(Locale.getDefault()) -> R.drawable.ic_marker_trip_started
                "TRIP END".toLowerCase(Locale.getDefault()) -> R.drawable.ic_marker_trip_end
                "TRUCK BREAKDOWN".toLowerCase(Locale.getDefault()) -> R.drawable.ic_marker_truck_breakdown
                "POLICE STOP".toLowerCase(Locale.getDefault()) -> R.drawable.ic_marker_truck_breakdown
                "DROP OFF DELIVERY".toLowerCase(Locale.getDefault()) -> R.drawable.ic_marker_package
                else -> R.drawable.ic_marker_overspeeding

            }
            )


        }
    }

    protected fun setupClusterManager(listTrucks: List<Trucks?>) {
        clusterManager.renderer = MarkerClusterRenderer(this, mMap, clusterManager)
        addTruckClusters(listTrucks)
        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)
        clusterManager.cluster()

        try {
            val boundsBuilder = LatLngBounds.Builder()


            truckMarkerManager?.values?.toList()?.forEach { item ->
                item.lastKnownLocation?.let {
                    boundsBuilder.include(latLngFromList(it.coordinates))
                }
            }

            val bounds = boundsBuilder.build()

            // begin new code:

            // begin new code:
            val width = resources.displayMetrics.widthPixels
            val height = resources.displayMetrics.heightPixels
            val padding = (width * 0.12).toInt() // offset from edges of the map 12% of screen


            val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
// end of new code

            mMap.animateCamera(cu)

//            if (currentDisplayMode == DisplayMode.ALL) {
//            mMap.uiSettings.isRotateGesturesEnabled = false
//            }
//                    setupClusterManager()
//                    moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.center, 10.0F))
        } catch (e: java.lang.Exception) {
            println("" + e.message)
        }
    }


    private fun addTruckClusters(listTrucks: List<Trucks?>) {
        if (listTrucks.isNotEmpty()) {
            listTrucks.forEach { e ->
                e?.lastKnownLocation?.let {
                    addTrucks(e)
                    clusterManager.addItem(TruckClusterItem(e))
                }
            }
        }
    }

    private fun addTrucks(model: Trucks?) {
        try {
            model?.lastKnownLocation?.let {
                if (it.coordinates[0] != 0.0) {
                    truckMarkerManager?.set(model.regNumber, model)
                }
            }
//            if (truckMarkerManager.size <= 50) {

//            }
        } catch (error: java.lang.Exception) {
            error.message?.let { Log.d("partner", it) }
        }

    }


    private fun setUpMQTT() {


        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.userName = CLOUDMQTT_USER
        mqttConnectOptions.password = CLOUDMQTT_PASS.toCharArray()


        mqttAndroidClient = MqttAndroidClient(applicationContext, CLOUDMQTT_HOST, "hbvhubiohpio")
        mqttAndroidClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                Log.i(NewBaseMapActivity::class.java.name, "connection lost")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                Log.i(NewBaseMapActivity::class.java.name, "topic: " + topic + ", msg: " + String(message.payload))
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                Log.i(NewBaseMapActivity::class.java.name, "msg delivered")
            }
        })

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.i(NewBaseMapActivity::class.java.name, "connect succeed")
                    subscribeTopic(mqttTopic)
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.i(NewBaseMapActivity::class.java.name, "connect failed")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }

    }

    fun subscribeTopic(topic: String?) {
        try {
            mqttAndroidClient.subscribe(topic, 0, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.i(NewBaseMapActivity::class.java.name, "subscribed succeed")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.i(NewBaseMapActivity::class.java.name, "subscribed failed")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun unSubscribe(topic: String) {
        try {
            val unsubToken = mqttAndroidClient.unsubscribe(topic)
            unsubToken.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Give your callback on unsubscribing here
                }
                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Give your callback on failure here
                }
            }
        } catch (e: MqttException) {
            // Give your callback on failure here
        }
    }


}