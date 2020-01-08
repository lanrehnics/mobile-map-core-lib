package com.kobo.mobile_map_core.mobile_map_core.map

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.firestore.GeoPoint
import java.lang.Exception
import com.google.android.gms.maps.model.LatLngBounds
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.maps.android.clustering.ClusterManager
import com.kobo.mobile_map_core.mobile_map_core.services.MapService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.models.ClearCommand
import com.kobo.mobile_map_core.mobile_map_core.models.DisplayMode
import com.kobo.mobile_map_core.mobile_map_core.models.TripStatus
import com.kobo.mobile_map_core.mobile_map_core.search.SearchActivity

import java.util.*


class MapsActivity : BaseMapActivity(), OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    companion object {
        const val TAG = "MapsActivity ::: ===>"
        val QUERY_CENTER = GeoPoint( 9.0649869,7.3277417) //Using Abuja As Centre
        const val QUERY_RADIUS = 855.0
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.partner_map_activity_main)
        context = this@MapsActivity

        configureCollectionReferenceForApp()

        mapService = MapService(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        tvTripId = findViewById(R.id.trip_id)
        tvTripStatus = findViewById(R.id.trip_status)
        tvCustName = findViewById(R.id.cust_name)
        tvPickUp = findViewById(R.id.pickup)
        tvDestination = findViewById(R.id.destination)
        tvDriverName = findViewById(R.id.driver_name)
        tvDriverMobile = findViewById(R.id.driver_mobile)
        tvRegNumber = findViewById(R.id.reg_number)
        tvEtt = findViewById(R.id.ett)
        backArrowButton = findViewById(R.id.back_arrow_button)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mMapView = mapFragment.view!!
        mapFragment.getMapAsync(this)

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.layout_bottom_sheet))
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        resetView()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        btnCloseTruckInfo.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                }
            }
        })


        toolbar = findViewById(R.id.toolbar)
        fabTripFilter = findViewById(R.id.fabTripFilter)
        fabOpenSearch = findViewById(R.id.fabSearch)
        fabMyLocation = findViewById(R.id.fabMyLocation)
        drawerLayout = findViewById(R.id.drawer_layout)

        navigationView = findViewById(R.id.navigationView)

        bottomSheetHeader = findViewById(R.id.bottomSheetHeader)

        val headerView: View = navigationView.getHeaderView(0)
        btnApply = headerView.findViewById(R.id.btn_apply)
        btnCloseDrawer = headerView.findViewById(R.id.btn_close_drawer)
        statusPositioned = headerView.findViewById(R.id.status_positioned)
        statusInpremise = headerView.findViewById(R.id.status_inpremise)
        statusLoaded = headerView.findViewById(R.id.status_loaded)
        btnCloseTruckInfo = findViewById(R.id.btn_close_truck_info)
        btnCloseTruckInfo.visibility = View.INVISIBLE

        statusTransporting = headerView.findViewById(R.id.status_transporting)
        statusAtDestination = headerView.findViewById(R.id.status_at_destination)
        statusAvailable = headerView.findViewById(R.id.status_available)

        tripFilterLinearLayout = headerView.findViewById(R.id.tripFilterLinearLayout)

        activeTrips = headerView.findViewById(R.id.active_trips)
        flaggedTrips = headerView.findViewById(R.id.flagged_trips)

        toggle = object : ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.open,
                R.string.close
        ) {
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)

                //Todo Add Animation
                fabTripFilter.visibility = View.VISIBLE
                fabMyLocation.visibility = View.VISIBLE
//                fabOpenSearch.visibility = View.VISIBLE
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                //Todo Add Animation
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
                if (fabMyLocation.visibility == View.VISIBLE) {
                    fabTripFilter.visibility = View.INVISIBLE
                    fabMyLocation.visibility = View.INVISIBLE
                    fabOpenSearch.visibility = View.INVISIBLE

                }
            }
        }

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        fabTripFilter.setOnClickListener(this)
        fabMyLocation.setOnClickListener(this)
        fabOpenSearch.setOnClickListener(this)
        btnApply.setOnClickListener(this)
        btnApply.visibility = View.INVISIBLE
        btnCloseDrawer.setOnClickListener(this)
        statusPositioned.setOnCheckedChangeListener(this)
        statusInpremise.setOnCheckedChangeListener(this)
        statusLoaded.setOnCheckedChangeListener(this)
        statusTransporting.setOnCheckedChangeListener(this)
        statusAtDestination.setOnCheckedChangeListener(this)
        statusAvailable.setOnCheckedChangeListener(this)
        activeTrips.setOnCheckedChangeListener(this)
        flaggedTrips.setOnCheckedChangeListener(this)
        bottomSheetHeader.setOnClickListener(this)
        btnCloseTruckInfo.setOnClickListener(this)
        backArrowButton.setOnClickListener(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        clusterManager = ClusterManager(this, googleMap)
        setClusterManagerClickListener()
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        //mMap.isTrafficEnabled = true
        mMap.isIndoorEnabled = false
        mMap.isBuildingsEnabled = true
//        mMap.uiSettings.isCompassEnabled = true
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))

        mMap.uiSettings.setAllGesturesEnabled(true)
//        mMap.addMarker(MarkerOptions()
//            .position(headOffice)
//            .title("Head office")
//            .icon(BitmapDescriptorFactory.fromResource(R.drawable.kobo_truck_blue))
//        )
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(headOffice))
        mMap.moveCamera(
                CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                                .target(googleMap.cameraPosition.target)
                                .zoom(15f)
//                .bearing(30f)
//                .tilt(45f)
                                .build()
                )
        )
//        loadTrucks()

//        bootStrapPickupStationsAndTrucks()

//        polyLineList = decodePoly("")
        mMap.setOnMarkerClickListener(this)
        setUpLocationPermission()
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
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

    override fun onMarkerClick(marker: Marker): Boolean {

        backArrowButton.visibility = View.INVISIBLE
        btnCloseTruckInfo.visibility = View.VISIBLE
//        selectedMarker = marker

//        if (truckMarkers.containsKey(marker)) {
//            btnCloseTruckInfo.visibility = View.VISIBLE
//            selectedTruck = truckMarkers[marker]!!
//            currentDisplayMode = DisplayMode.SINGLE
//            setTruckDetails()
//            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//            }
//            val destinationLatitude: Double =
//                    selectedTruck.d.deliveryStation.location.coordinates[0]
//            val destinationLongitude: Double =
//                    selectedTruck.d.deliveryStation.location.coordinates[1]
//
//            if (destinationLatitude > 0 && destinationLongitude > 0)
//                GlobalScope.launch(Dispatchers.Main) {
//                    try {
//                        val origin = GeoPoint(marker.position.latitude, marker.position.longitude)
//                        val destination = GeoPoint(destinationLatitude, destinationLongitude)
//                        val polyLineList = mapService.getPolyline(origin, destination)
//                        if (polyLineList.isNotEmpty()) {
//                            drawPolyLine(polyLineList)
//                        }
//                        removeOtherMarkersExcept(marker)
//                    } catch (e: Exception) {
//                        Log.e(TAG, "Error fetching customers locations ${e.message}")
//                    }
//                }
//
//        } else {
//            marker.showInfoWindow()
//        }

        return true
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.fabTripFilter -> {
                tripFilterLinearLayout.visibility = View.VISIBLE
                drawerLayout.openDrawer(GravityCompat.END)
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END)
                }
            }

            R.id.fabSearch -> {
                val searchIntent = Intent(this, SearchActivity::class.java)
                startActivity(searchIntent)
            }
            R.id.fabMyLocation -> {
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                } catch (e: Exception) {

                }
            }

            R.id.btn_apply -> {
                if (!isMappedFiltered) {
                    if (listTripStatusFilter.isNotEmpty()) {


                        val filteredTruckList = truckMarkerManager.values.toList().filter { truckModel ->

                            val activeTrip: String =
                                    if (truckModel.d.active == 1) TripStatus.STATUS_ACTIVE_TRIPS else ""
                            val flaaggedTrip: String =
                                    if (truckModel.d.flagged) TripStatus.STATUS_FLAGGED_TRIP else ""

                            listTripStatusFilter.contains(
                                    truckModel.d.status.toLowerCase(
                                            Locale.getDefault()
                                    )
                            ) ||
                                    listTripStatusFilter.contains(activeTrip.toLowerCase(Locale.getDefault())) ||
                                    listTripStatusFilter.contains(flaaggedTrip.toLowerCase(Locale.getDefault()))
                        }

                        if (filteredTruckList.isNotEmpty()) {

                            clearMapAndData(ClearCommand.MAP)
                            addClusters(filteredTruckList)
                            val boundsBuilder = LatLngBounds.Builder()

                            filteredTruckList.map { model ->
                                boundsBuilder.include(
                                        LatLng(
                                                model.l.latitude,
                                                model.l.longitude
                                        )
                                )
                            }
                            val bounds = boundsBuilder.build()
                            clusterManager.cluster()
                            val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)

                            mMap.animateCamera(cu)

                        } else {
                            showMessage(this, "No truck found")
                        }
                    }
                    btnApply.text = "Clear Filter"

                    isMappedFiltered = true

                } else {

                    bootStrapPickupStationsAndTrucks()

                    btnApply.text = "Apply Filter"

                    statusPositioned.isChecked = false
                    statusInpremise.isChecked = false
                    statusLoaded.isChecked = false
                    statusTransporting.isChecked = false
                    statusAtDestination.isChecked = false
                    statusAvailable.isChecked = false
                    flaggedTrips.isChecked = false
                    activeTrips.isChecked = false


                    customersLocations?.let {
                        it.data.locations.forEach { pickUpStation ->
                            addStations(pickUpStation)
                        }
                    }


                }
                drawerLayout.closeDrawer(GravityCompat.END)
            }

            R.id.btn_close_drawer -> {
                drawerLayout.closeDrawer(GravityCompat.END)
            }

            R.id.bottomSheetHeader -> {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            R.id.btn_close_truck_info -> {
                try {
                    if (isSearchOn) {

                        isSearchOn = false
                        resetView()

                    } else {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                } catch (e: Exception) {
                }
            }

            R.id.back_arrow_button -> {
                finish()
            }

            else -> { // Note the block
                print("Button Not Handled")
            }
        }
    }

    override fun onCheckedChanged(checkBox: CompoundButton?, value: Boolean) {
        isMappedFiltered = false
        btnApply.text = "Apply Filter"
        when (checkBox!!.id) {
            R.id.status_positioned -> {
                manageTripFilterParam(
                        TripStatus.STATUS_POSITIONED.toLowerCase(Locale.getDefault()),
                        value
                )
            }
            R.id.status_inpremise -> {
                manageTripFilterParam(
                        TripStatus.STATUS_IN_PREMISE.toLowerCase(Locale.getDefault()),
                        value
                )
            }
            R.id.status_loaded -> {
                manageTripFilterParam(
                        TripStatus.STATUS_LOADED.toLowerCase(Locale.getDefault()),
                        value
                )
            }
            R.id.status_transporting -> {
                manageTripFilterParam(
                        TripStatus.STATUS_TRANSPORTING.toLowerCase(Locale.getDefault()),
                        value
                )
            }
            R.id.status_at_destination -> {
                manageTripFilterParam(
                        TripStatus.STATUS_AT_DESTINATION.toLowerCase(Locale.getDefault()),
                        value
                )
            }
            R.id.status_available -> {
                manageTripFilterParam(
                        TripStatus.STATUS_AVAILABLE.toLowerCase(Locale.getDefault()),
                        value
                )
            }
            R.id.active_trips -> {
                manageTripFilterParam(
                        TripStatus.STATUS_ACTIVE_TRIPS.toLowerCase(Locale.getDefault()),
                        value
                )
            }
            R.id.flagged_trips -> {
                manageTripFilterParam(
                        TripStatus.STATUS_FLAGGED_TRIP.toLowerCase(Locale.getDefault()),
                        value
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> Log.i(TAG, "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    mMap.isMyLocationEnabled = true
                    fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                        // Got last known location. In some rare situations this can be null.
                        // 3
                        if (location != null) {
//                lastLocation = location
                            val currentLatLng = LatLng(location.latitude, location.longitude)
                            mMap.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                            currentLatLng,
                                            12f
                                    )
                            )
                            bootStrapPickupStationsAndTrucks()
                        }
                    }
                }
                else -> Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setClusterManagerClickListener() {
        clusterManager.setOnClusterClickListener {
            val items = it.items
            val itemsList = mutableListOf<String>()
            for (item in items) {
                itemsList.add((item as MarkerClusterItem).title)
            }
            mMap.animateCamera(
                    CameraUpdateFactory.newLatLng(it.position),
                    object : GoogleMap.CancelableCallback {
                        override fun onFinish() {
                            ListViewDialog(context, itemsList)
                        }

                        override fun onCancel() {}
                    })
            true
        }

        clusterManager.setOnClusterItemClickListener {
            mMap.animateCamera(
                    CameraUpdateFactory.newLatLng(it.position),
                    object : GoogleMap.CancelableCallback {
                        override fun onFinish() {
                            backArrowButton.visibility = View.INVISIBLE
                            btnCloseTruckInfo.visibility = View.VISIBLE

                            // Clear all geopoints and clustered markers
                            clearMapAndData(ClearCommand.MAP)
                            selectedTruck = truckMarkerManager[it.title]!!

                            // clusterManager has access to display markers
                            // try extract the selected marker from the marker manager in clusterManager
                            selectedMarker = getMarkerFromClusterCollections(it)


//                        if (truckMarkers.containsKey(marker)) {
                            btnCloseTruckInfo.visibility = View.VISIBLE
                            currentDisplayMode = DisplayMode.SINGLE
                            setTruckDetails()
                            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                            }
                            val destinationLatitude: Double =
                                    selectedTruck!!.d.deliveryStation.location.coordinates[0]
                            val destinationLongitude: Double =
                                    selectedTruck!!.d.deliveryStation.location.coordinates[1]


                            val origin = toGeoPoint(selectedMarker.position)

                            val marker = mMap.addMarker(
                                    MarkerOptions()
                                            .position(toLatLng(origin))
                                            .title(selectedTruck!!.d.reg_number)
                                            .rotation(selectedTruck!!.d.bearing.toFloat())
                                            .icon(truckFromStatus(selectedTruck!!))
                            )

                            // Reset marker after adding new marker
                            selectedMarker = marker
                            markerManager[selectedTruck!!.d.reg_number] = marker

                            val destination =
                                    GeoPoint(destinationLatitude, destinationLongitude)

                            if (destinationLatitude > 0 && destinationLongitude > 0)
                                GlobalScope.launch(Dispatchers.Main) {
                                    try {
                                        val polyLineList =
                                                mapService.getPolyline(origin, destination)
                                        if (polyLineList.isNotEmpty()) {
                                            drawPolyLine(polyLineList)
                                        }
                                    } catch (e: Exception) {
                                        Log.e(
                                                TAG,
                                                "Error fetching customers locations ${e.message}"
                                        )
                                    }
                                }

//                        } else {
//                            marker.showInfoWindow()
//                        }
                        }

                        override fun onCancel() {}
                    })
            true
        }
    }
}
