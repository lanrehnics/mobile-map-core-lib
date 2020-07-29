package com.kobo.mobile_map_core.mobile_map_core.ui.map

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.models.MQTTPayload
import com.kobo.mobile_map_core.mobile_map_core.data.models.NavigationData
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Trips
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import com.mapbox.api.directions.v5.models.BannerInstructions
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.services.android.navigation.ui.v5.NavigationView
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback
import com.mapbox.services.android.navigation.ui.v5.listeners.BannerInstructionsListener
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*


class NavigationActivity(
) : AppCompatActivity(), OnNavigationReadyCallback, NavigationListener, ProgressChangeListener, BannerInstructionsListener {
    private var navigationView: NavigationView? = null
    private var spacer: View? = null
    private var speedWidget: TextView? = null

    //    private var fabNightModeToggle: FloatingActionButton? = null
//    private var fabStyleToggle: FloatingActionButton? = null
    private var bottomSheetVisible = true
    private var instructionListShown = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val styleCycle = StyleCycle()

    private lateinit var mqttAndroidClient: MqttAndroidClient
    private lateinit var mqttTopic: String

    private lateinit var ORIGIN: Point
    private lateinit var DESTINATION: Point
    private var navigationData: NavigationData? = null


    private val PRODUCTKEY = "a11xsrW****"
    private val DEVICENAME = "paho_android"
    private val DEVICESECRET = "tLMT9QWD36U2SArglGqcHCDK9rK9****"

    private val CLOUDMQTT_HOST = "tcp://smart-journalist.cloudmqtt.com:1883"
    private val CLOUDMQTT_PORT = "1883"
    private val CLOUDMQTT_USER = "mobileand"
    private val CLOUDMQTT_PASS = "kbMob20@ge"



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar)
//        initNightMode()
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setUpLocationPermission()
        navigationData = intent.getSerializableExtra(NAVIGATION_DATA) as NavigationData?

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        initNavParams()
        setContentView(R.layout.activity_navigation)
        setUpMQTT()
        navigationView = findViewById(R.id.navigationView)
        navigationView!!.setStatusBarBackgroundColor(resources.getColor(R.color.colorPrimary))
//        fabNightModeToggle = findViewById(R.id.fabToggleNightMode)
//        fabStyleToggle = findViewById(R.id.fabToggleStyle)
        speedWidget = findViewById(R.id.speed_limit)
        spacer = findViewById(R.id.spacer)
        setSpeedWidgetAnchor(R.id.summaryBottomSheet)

        navigationView!!.onCreate(savedInstanceState)
    }

    private fun initNavParams() {
        if (navigationData != null) {
            prepareNavigationParams(navigationData)
        }
    }

    private fun setUpLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), BattlefieldLandingActivity.LOCATION_PERMISSION_REQUEST_CODE)
            return

        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    ORIGIN = NewBaseMapActivity.toPoint(LatLng(location.latitude, location.longitude))
                    val initialPosition = CameraPosition.Builder()
                            .target(NewBaseMapActivity.toMapBoxLatLng(ORIGIN))
                            .zoom(INITIAL_ZOOM.toDouble())
                            .build()
                    navigationView!!.initialize(this, initialPosition)

                }
            }
        }
    }


    override fun onNavigationReady(isRunning: Boolean) {
        fetchRoute()
    }

    public override fun onStart() {
        super.onStart()
        navigationView!!.onStart()
    }

    public override fun onResume() {
        super.onResume()
        navigationView!!.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        navigationView!!.onLowMemory()
    }

    override fun onBackPressed() {
        // If the navigation view didn't need to do anything, call super
        if (!navigationView!!.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigationView!!.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        navigationView!!.onRestoreInstanceState(savedInstanceState)
    }

    public override fun onPause() {
        super.onPause()
        navigationView!!.onPause()
    }

    public override fun onStop() {
        super.onStop()
        navigationView!!.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        navigationView!!.onDestroy()
        if (isFinishing) {
            saveNightModeToPreferences(AppCompatDelegate.MODE_NIGHT_AUTO)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_TIME)
        }
    }

    override fun onCancelNavigation() {
        // Navigation canceled, finish the activity
        finish()
    }

    override fun onNavigationFinished() {
        // Intentionally empty
    }

    override fun onNavigationRunning() {
        // Intentionally empty
    }

    override fun onProgressChange(location: Location, routeProgress: RouteProgress) {
        setSpeed(location)
        publishMessage(
                MQTTPayload(
                        regNumber = navigationData?.regNumber,
                        tripId = navigationData?.tripId,
                        tripReadId = navigationData?.tripReadId,
                        bearing = location.bearing,
                        lng = location.longitude,
                        lat = location.latitude,
                        speed = location.speed,
                        accuracy = location.accuracy,
                        distanceTraveled = routeProgress.distanceTraveled(),
                        durationRemaining = routeProgress.durationRemaining(),
                        distanceRemaining = routeProgress.distanceRemaining()
//                        distance = location.distanceTo(destination)
                )
        )
    }
//
//    override fun onInstructionListVisibilityChanged(shown: Boolean) {
//        instructionListShown = shown
//        speedWidget!!.visibility = if (shown) View.GONE else View.VISIBLE
//        if (instructionListShown) {
//            fabNightModeToggle!!.hide()
//        } else if (bottomSheetVisible) {
//            fabNightModeToggle!!.show()
//        }
//    }

//    fun willVoice(announcement: VoiceInstructions?): VoiceInstructions {
//        return VoiceInstructions.builder().announcement("All announcements will be the same.").build()
//    }

    override fun willDisplay(instructions: BannerInstructions): BannerInstructions {
        return instructions
    }

    private fun startNavigation(directionsRoute: DirectionsRoute) {
        val options = NavigationViewOptions.builder()
                .navigationListener(this)
                .directionsRoute(directionsRoute)
                .shouldSimulateRoute(true)
                .progressChangeListener(this)
//                .instructionListListener(this)
//                .speechAnnouncementListener(this)
                .bannerInstructionsListener(this)
                .offlineRoutingTilesPath(obtainOfflineDirectory())
                .offlineRoutingTilesVersion(obtainOfflineTileVersion())
        setBottomSheetCallback(options)
//        setupStyleFab()
        setupNightModeFab()
        navigationView!!.startNavigation(options.build())
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray) {
        Log.i(BattlefieldLandingActivity.TAG, "onRequestPermissionResult")
        if (requestCode == BattlefieldLandingActivity.LOCATION_PERMISSION_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> Log.i(BattlefieldLandingActivity.TAG, "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                        if (location != null) {
                            ORIGIN = NewBaseMapActivity.toPoint(LatLng(location.latitude, location.longitude))
                        }
                    }
                }
                else -> Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtainOfflineDirectory(): String {
        val offline = Environment.getExternalStoragePublicDirectory("Offline")
        if (!offline.exists()) {
            Timber.d("Offline directory does not exist")
            offline.mkdirs()
        }
        return offline.absolutePath
    }

    private fun obtainOfflineTileVersion(): String? {
        return PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.offline_version_key), "")
    }

    private fun fetchRoute() {

//        initNavParams()

        NavigationRoute.builder(this)
                .accessToken(resources.getString(R.string.mapbox_access_token))
                .origin(ORIGIN)
                .destination(DESTINATION)
                .alternatives(true)
                .build()
                .getRoute(object : Callback<DirectionsResponse> {
                    override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                        startNavigation(response.body()!!.routes().first())

                    }

                    override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {

                    }
                })

    }

    /**
     * Sets the anchor of the spacer for the speed widget, thus setting the anchor for the speed widget
     * (The speed widget is anchored to the spacer, which is there because padding between items and
     * their anchors in CoordinatorLayouts is finicky.
     *
     * @param res resource for view of which to anchor the spacer
     */
    private fun setSpeedWidgetAnchor(@IdRes res: Int) {
        val layoutParams = spacer!!.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.anchorId = res
        spacer!!.layoutParams = layoutParams
    }

    private fun setBottomSheetCallback(options: NavigationViewOptions.Builder) {
        options.bottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        bottomSheetVisible = false
//                        fabNightModeToggle!!.hide()
                        setSpeedWidgetAnchor(R.id.recenterBtn)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> bottomSheetVisible = true
                    BottomSheetBehavior.STATE_SETTLING -> if (!bottomSheetVisible) {
                        // View needs to be anchored to the bottom sheet before it is finished expanding
                        // because of the animation
//                        fabNightModeToggle!!.show()
                        setSpeedWidgetAnchor(R.id.summaryBottomSheet)
                    }
                    else -> return
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun setupNightModeFab() {
//        fabNightModeToggle!!.setOnClickListener { view: View? -> toggleNightMode() }
    }

//    private fun setupStyleFab() {
//        fabStyleToggle!!.setOnClickListener { view: View? -> navigationView!!.retrieveNavigationMapboxMap()!!.retrieveMap().setStyle(styleCycle.getNextStyle()) }
//    }

    private class StyleCycle {
        private var index = 0
        private val nextStyle: String
            private get() {
                index++
                if (index == STYLES.size) {
                    index = 0
                }
                return style
            }

        private val style: String
            private get() = STYLES[index]

        companion object {
            private val STYLES = arrayOf(
                    Style.MAPBOX_STREETS,
                    Style.OUTDOORS,
                    Style.LIGHT,
                    Style.DARK,
                    Style.SATELLITE_STREETS
            )

        }
    }

    private fun toggleNightMode() {
        val currentNightMode = currentNightMode
        alternateNightMode(currentNightMode)
    }

    private fun initNightMode() {
        val nightMode = retrieveNightModeFromPreferences()
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    private val currentNightMode: Int
        private get() = (resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK)

    private fun alternateNightMode(currentNightMode: Int) {
        val newNightMode: Int
        newNightMode = if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }
        saveNightModeToPreferences(newNightMode)
        recreate()
    }

    private fun retrieveNightModeFromPreferences(): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        return preferences.getInt(getString(R.string.current_night_mode), AppCompatDelegate.MODE_NIGHT_AUTO)
    }

    private fun saveNightModeToPreferences(nightMode: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.putInt(getString(R.string.current_night_mode), nightMode)
        editor.apply()
    }

    private fun setSpeed(location: Location) {
        val string = String.format("%d\nMPH", (location.speed * 2.2369).toInt())
        val mphTextSize = resources.getDimensionPixelSize(R.dimen.mph_text_size)
        val speedTextSize = resources.getDimensionPixelSize(R.dimen.speed_text_size)
        val spannableString = SpannableString(string)
        spannableString.setSpan(AbsoluteSizeSpan(mphTextSize),
                string.length - 4, string.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        spannableString.setSpan(AbsoluteSizeSpan(speedTextSize),
                0, string.length - 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        speedWidget!!.text = spannableString
        if (!instructionListShown) {
            speedWidget!!.visibility = View.VISIBLE
        }
    }

    companion object {
        const val NAVIGATION_DATA = "NAVIGATION_DATA"


        //        private val ORIGIN = Point.fromLngLat(3.325125, 6.472660)
//        private var DESTINATION = Point.fromLngLat(3.350918, 6.555414)
        private const val INITIAL_ZOOM = 16
    }

    private fun setUpMQTT() {

        val mqttConnectOptions = MqttConnectOptions()

        mqttConnectOptions.userName = CLOUDMQTT_USER
        mqttConnectOptions.password = CLOUDMQTT_PASS.toCharArray()


        mqttAndroidClient = MqttAndroidClient(applicationContext, CLOUDMQTT_HOST, "hbvhubiohpio")
        mqttAndroidClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                Log.i(NavigationActivity::class.java.name, "connection lost")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                Log.i(NavigationActivity::class.java.name, "topic: " + topic + ", msg: " + String(message.payload))
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                Log.i(NavigationActivity::class.java.name, "msg delivered")
            }
        })

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.i(NavigationActivity::class.java.name, "connect succeed")
//                    subscribeTopic("server/track/SMK6RTT7")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.i(NavigationActivity::class.java.name, "connect failed")
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
                    Log.i(NavigationActivity::class.java.name, "subscribed succeed")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.i(NavigationActivity::class.java.name, "subscribed failed")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publishMessage(payload: MQTTPayload) {
        val stringifyPayload = Gson().toJson(payload)
        try {
            if (!mqttAndroidClient.isConnected) {
                mqttAndroidClient.connect()
            }
            val message = MqttMessage()
            message.payload = stringifyPayload.toByteArray()
            message.qos = 0
            mqttAndroidClient.publish(mqttTopic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.i(NavigationActivity::class.java.name, "publish succeed! ")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.i(NavigationActivity::class.java.name, "publish failed!")
                }
            })
        } catch (e: MqttException) {
            Log.e(NavigationActivity::class.java.name, e.toString())
            e.printStackTrace()
        }
    }


//    private fun prepareNavigationParams(truckInfo: Trucks?) {
//        DESTINATION = NewBaseMapActivity.toPoint(truckInfo!!.tripDetail.deliveryLocation.coordinates)
//        mqttTopic = "server/track/${truckInfo.regNumber.toLowerCase(Locale.getDefault())}"
//
//    }

    private fun prepareNavigationParams(navigationData: NavigationData?) {
//        ORIGIN = NewBaseMapActivity.toPoint(tripInfo?.tripDetail?.pickupLocation?.coordinates)
        DESTINATION = NewBaseMapActivity.toPoint(navigationData?.destination)
        mqttTopic = "server/track/${navigationData?.regNumber?.toLowerCase(Locale.getDefault())}"
//        DESTINATION = Point.fromLngLat(3.350918, 6.555414)
    }

}
