package com.kobo.mobile_map_core.mobile_map_core.ui.map

import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.Autocomplete
import SelectedAddrss
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.ClusterManager
import com.kobo.mobile_map_core.mobile_map_core.MobileMapCorePlugin
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiHelper
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiServiceImpl
import com.kobo.mobile_map_core.mobile_map_core.data.models.ClearCommand
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Location
import com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model.AssetClasses
import com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model.Size
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.TruckData
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.TruckDataConst
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview.Overview
import com.kobo.mobile_map_core.mobile_map_core.data.services.MapService
import com.kobo.mobile_map_core.mobile_map_core.ui.base.ViewModelFactory
import com.kobo.mobile_map_core.mobile_map_core.ui.fragments.FragmentSearchAvailableTrucks
import com.kobo.mobile_map_core.mobile_map_core.ui.fragments.SearchForPlaces
import com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel.MapLandingViewModel
import com.xdev.mvvm.utils.Status
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.battlefield_landing_activity_main.*
import kotlinx.android.synthetic.main.fragment_user_options.*
import kotlinx.android.synthetic.main.search_available_truck.*
import timber.log.Timber
import java.lang.Exception


class BattlefieldLandingActivity : BaseMapActivity(), OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, View.OnClickListener, SearchForPlaces.OnAddressSelectedListener, FragmentSearchAvailableTrucks.SwitchToMapClickListener, FragmentSearchAvailableTrucks.OnTruckInfoClickedListener, FragmentSearchAvailableTrucks.OnAvailableTruckCloseButtonClickListener {

    private lateinit var mapLandingViewModel: MapLandingViewModel
    private lateinit var assetClasses: AssetClasses

    //    private lateinit var selectedSize: Size
    private var searchRadius: Int? = 1
    private var searchForAvailableTrucks: Boolean = false
    private lateinit var trkData: TruckData
    private lateinit var shaper: SharedPreferences
    private var overviewW: Overview? = null

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is SearchForPlaces) {
            fragment.setOnAddressSelectedListener(this)
        }
    }

    companion object {
        const val TAG = "MapsActivity : :: ===>"
        const val QUERY_RADIUS = 855.0
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        var searchMode = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.battlefield_landing_activity_main)
        context = this@BattlefieldLandingActivity

        shaper = this.let {
            PreferenceManager.getDefaultSharedPreferences(it)
        }


        //Initialize views and click listeners
        initializeViews()
        setupViewModel()
        setupFetchGroupedAssetObserver()


        //Pass context

        // Configure som initialization base on app type
//        configureCollectionReferenceForApp()


        // Initialize map, services, bottom sheet and drawer
        mapService = MapService(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mMapView = mapFragment.requireView()
        mapFragment.getMapAsync(this)

        overviewBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        searchBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        truckDetailsBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        overviewBottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
//                        resetView()
//                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
//                        btnCloseTruckInfo.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                }
            }
        })

        searchBottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                        if (!searchForAvailableTrucks) {
                            overviewBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                            searchBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                        }

                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        if (!searchForAvailableTrucks)
                            overviewBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                }
            }
        })


    }

    private fun setUpSpinner(list: List<AssetClasses>) {

        val spinnerAdapter: ArrayAdapter<AssetClasses> = ArrayAdapter(this, R.layout.item_spinner, list)

        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                assetClasses = parent.selectedItem as AssetClasses
//                setUpSpinner2(assetClasses.size)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


    }

//    private fun setUpSpinner2(list: List<Size>) {
//
//        val spinnerAdapter: ArrayAdapter<Size> = ArrayAdapter(this, R.layout.item_spinner, list)
//
//        spinner2.adapter = spinnerAdapter
//        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                selectedSize = parent.selectedItem as Size
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
//
//
//    }

    private fun setupViewModel() {
        mapLandingViewModel = ViewModelProviders.of(
                this,
                ViewModelFactory(ApiHelper(ApiServiceImpl(this)))
        ).get(MapLandingViewModel::class.java)
    }


    private fun setupFetchGroupedAssetObserver() {
        mapLandingViewModel.getGroupedAssetClass().observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { response -> setUpSpinner(response.data.assetClasses) }!!
                }
                Status.LOADING -> {
//                    progressBar.visibility = View.VISIBLE
//                    recyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
                    //Handle Error
//                    progressBar.visibility = View.GONE
//                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun subscribeForReverseGeocode() {
        mapLandingViewModel.getReverseGeocode(currentLatLng).observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let { response -> setMyCurrentLocations(response.data.geocoded.location.address) }!!
                }
                Status.LOADING -> {
//                    progressBar.visibility = View.VISIBLE
//                    recyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
                    //Handle Error
//                    progressBar.visibility = View.GONE
//                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun setMyCurrentLocations(address: String) {
        globalSelectedAdd.pickUp?.description = address
        etPickUpLocation.text = address.toEditable()
        tvMyCurrentLocation.text = address
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener {
            currentLatLng = it
            fetchAndSubscribeForLocationOverview()
        }
        clusterManager = ClusterManager(this, googleMap)
        setClusterManagerClickListener()
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        //mMap.isTrafficEnabled = true
        mMap.isIndoorEnabled = false
        mMap.isBuildingsEnabled = true
//        mMap.uiSettings.isCompassEnabled = true
//        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style))

        mMap.uiSettings.setAllGesturesEnabled(true)

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
        mMap.setOnMarkerClickListener(this)
        setUpLocationPermission()


    }

    private fun setUpLocationPermission() {
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
                    currentLatLng = LatLng(location.latitude, location.longitude)

                    globalSelectedAdd.pickUp?.latLng = currentLatLng
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))

                    subscribeForReverseGeocode()
                    fetchAndSubscribeForLocationOverview()

                    //TODO implement map overview here ...
//                    bootStrapPickupStationsAndTrucks()
                }
            }

            val locationButton = (mMapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(Integer.parseInt("2"))

            locationButton.visibility = View.INVISIBLE
//
//            val rlp=locationButton.layoutParams as (RelativeLayout.LayoutParams)
//            // position on right bottom
//            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0)
//            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
//            rlp.setMargins(0,0,60,30)
        }
    }

    private fun fetchAndSubscribeForLocationOverview() {


        tvHello.text = "Hello ${shaper.getString(MobileMapCorePlugin.KEY_USER_FIRST_NAME, "")}"
        val userTypeAndId = shaper.getString(MobileMapCorePlugin.KEY_USER_TYPE_AND_ID, "")

        mapLandingViewModel.getLocationOverview(userTypeAndId!!, currentLatLng).observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    pBar.visibility = View.INVISIBLE
                    switchToList.visibility = View.INVISIBLE
                    it.data?.let { response -> bootstrapLocationOverview(response.data?.overview) }!!
                }
                Status.LOADING -> {
                    pBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    pBar.visibility = View.INVISIBLE
                    println("")
                    //Handle Error
                }
            }
        })

    }

    override fun onMarkerClick(marker: Marker): Boolean {

//        backArrowButton.visibility = View.INVISIBLE
//        btnCloseTruckInfo.visibility = View.VISIBLE
        return true
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
//            R.id.fabTripFilter -> {
//                tripFilterLinearLayout.visibility = View.VISIBLE
////                drawerLayout.openDrawer(GravityCompat.END)
//                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
//                    drawerLayout.closeDrawer(GravityCompat.END)
//                }
//            }

//            R.id.fabSearch -> {
//                val searchIntent = Intent(this, SearchActivity::class.java)
//                startActivity(searchIntent)
//            }
//            R.id.fabMyLocation -> {
//                try {
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
//                } catch (e: Exception) {
//
//                }
//            }

//            R.id.btn_apply -> {
//                if (!isMappedFiltered) {
//                    if (listFilterTerms.isNotEmpty()) {
//                        val filteredTruckList = truckMarkerManager.values.toList().filter { truckModel ->
//                            val activeTrip: String = if (truckModel.d.active == 1) TripStatus.STATUS_ACTIVE_TRIPS else ""
//                            val flaggedTrip: String = if (truckModel.d.flagged) TripStatus.STATUS_FLAGGED_TRIP else ""
//
//                            listFilterTerms.contains(truckModel.d.status.toLowerCase(Locale.getDefault()))
//                                    || listFilterTerms.contains(activeTrip.toLowerCase(Locale.getDefault()))
//                                    || listFilterTerms.contains(flaggedTrip.toLowerCase(Locale.getDefault()))
//                                    || listFilterTerms.contains(truckModel.d.resourceType)
//                        }
//
//                        if (filteredTruckList.isNotEmpty()) {
//                            clearMapAndData(ClearCommand.MAP)
////                            addClusters(filteredTruckList)
//                            val boundsBuilder = LatLngBounds.Builder()
//
//                            filteredTruckList.map { model ->
//                                boundsBuilder.include(
//                                        LatLng(
//                                                model.l.latitude,
//                                                model.l.longitude
//                                        )
//                                )
//                            }
//                            val bounds = boundsBuilder.build()
//                            clusterManager.cluster()
//                            val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
//
//                            mMap.animateCamera(cu)
//
//                        } else {
//                            showMessage(this, "No truck found")
//                        }
//                    }
//                    btnApply.text = "Clear Filter"
//
//                    isMappedFiltered = true
//
//                } else {
//
//                    isMappedFilteredByCustomer = false
//                    isMappedFilteredByStations = false
//
//                    bootStrapPickupStationsAndTrucks()
//
//                    btnApply.text = "Apply Filter"
//
//                    statusPositioned.isChecked = false
//                    statusInpremise.isChecked = false
//                    statusLoaded.isChecked = false
//                    statusTransporting.isChecked = false
//                    statusAtDestination.isChecked = false
//                    statusAvailable.isChecked = false
//                    flaggedTrips.isChecked = false
//                    activeTrips.isChecked = false
//                    customers.isChecked = false
//                    stations.isChecked = false
//
//
////                    customersLocations?.let {
////                        it.data.locations.forEach { pickUpStation ->
////                            addStations(pickUpStation, KoboLocationType.CUSTOMER)
////                        }
////                    }
//
//
//                }
//                drawerLayout.closeDrawer(GravityCompat.END)
//            }

            R.id.bottomSheetHeader -> {
                if (overviewBottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    overviewBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    overviewBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }


            R.id.btnSearchButton -> {
                fetchAvailableTruck(globalSelectedAdd.pickUp?.latLng, globalSelectedAdd.destination?.latLng, searchRadius, assetClasses.type)
            }

            R.id.btnCloseTruckInfo -> {
                searchForAvailableTrucks = false
                btnCloseTruckInfo.visibility = View.INVISIBLE
                searchBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                if (truckDetailsBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
                    truckDetailsBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
//                    overviewBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                fetchAndSubscribeForLocationOverview()
            }
            else -> {
                print("Button Not Handled")
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> Log.i(TAG, "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
//                    mMap.isMyLocationEnabled = true
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

            if (it != null) {
                truckDetailsBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                val items = it.items
                val builder = LatLngBounds.Builder()
                for (item in items) {
                    builder.include(item.position)
                }
                val bounds = builder.build()
                val mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
                mMap.animateCamera(mCameraUpdate)
            }
            true
        }
        clusterManager.setOnClusterItemClickListener {
            mMap.animateCamera(
                    CameraUpdateFactory.newLatLng(it.position),
                    object : GoogleMap.CancelableCallback {
                        override fun onFinish() {

                            try {
                                selectedTruck = truckMarkerManager[it.title]!!
                                truckDetailsBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED

                                setTruckDetails()
                            } catch (e: Exception) {
                                Timber.i(e.message.toString())
                            }

                        }
                        // clusterManager has access to display markers
                        // try extract the selected marker from the marker manager in clusterManager
//                            selectedMarker = getMarkerFromClusterCollections(it)


//                            if (selectedTruck?.d?.resourceType == "truck") {
//                                // Clear all geopoints and clustered markers
//                                clearMapAndData(ClearCommand.MAP)
////                                backArrowButton.visibility = View.INVISIBLE
////                                btnCloseTruckInfo.visibility = View.VISIBLE
////                                btnCloseTruckInfo.visibility = View.VISIBLE
//                                currentDisplayMode = DisplayMode.SINGLE
//                                setTruckDetails()
//                                if (overviewBottomSheet.state == BottomSheetBehavior.STATE_HIDDEN) {
//                                    overviewBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
//                                }
//                                val destinationLatitude: Double =
//                                        selectedTruck!!.d.deliveryStation.location.coordinates[0]
//                                val destinationLongitude: Double =
//                                        selectedTruck!!.d.deliveryStation.location.coordinates[1]
//
//
//                                val origin = toGeoPoint(selectedMarker.position)
//
//                                val marker = mMap.addMarker(
//                                        MarkerOptions()
//                                                .position(toLatLng(origin))
//                                                .title(selectedTruck!!.d.reg_number)
//                                                .rotation(selectedTruck!!.d.bearing.toFloat())
//                                                .icon(truckFromStatus(selectedTruck!!))
//                                )
//
//                                // Reset marker after adding new marker
//                                selectedMarker = marker
//                                markerManager[selectedTruck!!.d.reg_number] = marker
//
//                                val destination = GeoPoint(destinationLatitude, destinationLongitude)
//
//                                if (destinationLatitude > 0 && destinationLongitude > 0)
//                                    GlobalScope.launch(Dispatchers.Main) {
//                                        try {
//                                            val polyLineList =
//                                                    mapService.getPolyline(origin, destination)
//                                            if (polyLineList.isNotEmpty()) {
//                                                drawPolyLine(polyLineList)
//                                            }
//                                        } catch (e: Exception) {
//                                            Log.e(
//                                                    TAG,
//                                                    "Error fetching customers locations ${e.message}"
//                                            )
//                                        }
//                                    }
//
//                            } else {
//                                selectedMarker.showInfoWindow()
//                            }

                        override fun onCancel() {}
                    })
            true
        }
    }

    private fun initializeViews() {
        seekBar.progress = 1
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                searchRadius = i
                seekBarIndicator.text = "${i} KM"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })



        toolbar = findViewById(R.id.toolbar)

        bottomSheetHeader = findViewById(R.id.bottomSheetHeader)
        btnSearchButton = findViewById(R.id.btnSearchButton)


        overviewBottomSheet = BottomSheetBehavior.from(findViewById(R.id.layout_bottom_sheet))
        searchBottomSheet = BottomSheetBehavior.from(findViewById(R.id.searchAvailableTruckId))
        truckDetailsBottomSheet = BottomSheetBehavior.from(findViewById(R.id.dedicatedTruckBottomSheet))

        bottomSheetHeader.setOnClickListener(this)
        btnSearchButton.setOnClickListener(this)
        btnCloseTruckInfo.setOnClickListener(this)
    }

    fun openSearchAvailableTrucks(view: View) {
        overviewBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        searchBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun closeSearchBottomSheet(view: View) {
        overviewBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        searchBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }


    fun openTrackActiveTrips(view: View) {
        startActivity(Intent(this, TrackActiveTrips::class.java))
    }

    fun openDedicatedTruck(view: View) {
        startActivity(Intent(this, DedicatedTruckActivity::class.java))
    }

    fun openTruckNavigation(view: View) {
        startActivity(Intent(this, NavigationActivity::class.java))
    }

    fun setToLocation(view: View) {
        searchRadius = null
        editTextToLocation.visibility = View.VISIBLE
        seekBar.visibility = View.GONE
        calibration.visibility = View.GONE
    }

    fun setSearchRadius(view: View) {
        searchRadius = seekBar.progress
        editTextToLocation.visibility = View.GONE
        seekBar.visibility = View.VISIBLE
        calibration.visibility = View.VISIBLE
    }

    fun openFragmentSearchForPlacesForPickUpLocation(view: View) {
        searchMode = 0
        openAutoCompleteFragment(0, null)
    }

    fun openFragmentSearchForPlaces(view: View) {
        searchMode = 1
        openAutoCompleteFragment(1, globalSelectedAdd.pickUp?.description)
    }

    private fun openAutoCompleteFragment(mode: Int, pickUpAddressIfAvailable: String?) {
        val searchForPlaces = SearchForPlaces.newInstance(mode, pickUpAddressIfAvailable, overviewW)
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.mainMapHome, searchForPlaces, "searchForPlaces").addToBackStack("searchForPlaces").commit()
    }

    fun switchToListFromMap(view: View) {

        switchToList.visibility = View.INVISIBLE

        val fragmentSearchAvailableTrucks = FragmentSearchAvailableTrucks.newInstance(trkData)
        fragmentSearchAvailableTrucks.setSwitchToMapClickListener(this)
        fragmentSearchAvailableTrucks.setOnTruckInfoClickedListener(this)
        fragmentSearchAvailableTrucks.setOnAvailableTruckCloseButtonClickListener(this)
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.mainMapHome, fragmentSearchAvailableTrucks, "fragmentSearchAvailableTrucks").addToBackStack("fragmentSearchAvailableTrucks").commit()
    }


    override fun onBackPressed() {

        if (truckDetailsBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            truckDetailsBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        } else if (searchForAvailableTrucks) {

            val count = supportFragmentManager.backStackEntryCount
            if (count > 0) {
                supportFragmentManager.popBackStack()
            } else {
                fetchAndSubscribeForLocationOverview()
                searchForAvailableTrucks = false
                btnCloseTruckInfo.visibility = View.INVISIBLE
                searchBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onSelect(selectedAddress: SelectedAddrss) {
        when (selectedAddress.mode) {
            0 -> {
                globalSelectedAdd.pickUp = selectedAddress.pickUp
                etPickUpLocation.text = selectedAddress.pickUp?.description?.toEditable()
                selectedAddress.pickUp!!.placeId?.let { bootstrapLatLng(it, globalSelectedAdd.pickUp) }
            }
            else -> {
                searchRadius = null
                globalSelectedAdd.destination = selectedAddress.destination
                editTextToLocation.text = selectedAddress.destination?.description?.toEditable()
                selectedAddress.destination!!.placeId?.let { bootstrapLatLng(it, globalSelectedAdd.destination) }
            }
        }
        onBackPressed()
    }


    private fun bootstrapLatLng(placesId: String, add: Autocomplete?) {
        mapLandingViewModel.fetchLatLngFromPlacesId(placesId).observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { response -> setLatLngInfo(response.data.place.geometry.location, add) }!!
                }
                Status.LOADING -> {
//                    progressBar.visibility = View.VISIBLE
//                    recyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
                    println("")
                    //Handle Error
//                    progressBar.visibility = View.GONE
//                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    //TOdo Add validation for search not to be clickable until this is done
    private fun setLatLngInfo(location: Location, add: Autocomplete?) {
        add!!.latLng = LatLng(location.lat, location.lng)
    }


    private fun fetchAvailableTruck(origin: LatLng?, destination: LatLng?, radius: Int?, assetId: String) {
        mapLandingViewModel.fetchAvailableTruck(origin, destination, radius, assetId).observe(this, androidx.lifecycle.Observer {
            when (it.status) {

                Status.SUCCESS -> {
                    switchToList.visibility = View.VISIBLE
                    it.data?.let { response -> bootstrapAvailableTrucksOnMap(response.data.truckData) }!!
                }

                Status.LOADING -> {
                    btnSearchButton.startAnimation()
//                    progressBar.visibility = View.VISIBLE
//                    recyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
                    btnSearchButton.revertAnimation()
                    println("Error")
                    //Handle Error
//                    progressBar.visibility = View.GONE
//                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    private fun bootstrapLocationOverview(overview: Overview?) {
        overviewW = overview
        tvTruckOverviewAvailableTrucks.text = overview?.totalAvailableTrucks.toString()
        tvTruckOverviewActiveTrips.text = overview?.totalActiveTrucks.toString()
        clearMapAndData(ClearCommand.ALL)
        overview?.trucks?.let {
            setupClusterManager(it as List<Trucks>)
        }


        overview?.customerLocations?.let {
            it.forEach { cl ->
                cl?.location?.let { loc ->
                    mMap.addMarker(
                            MarkerOptions()
                                    .position(NewBaseMapActivity.toLatLngNotNull(loc.coordinates))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_customerlocation)))
                }
            }


        }

//        overview?.kobocareStations?.let {
//            it.forEach { cl ->
//                cl?.location?.let { loc ->
//                    mMap.addMarker(
//                            MarkerOptions()
//                                    .position(NewBaseMapActivity.toLatLngNotNull(loc.coordinates))
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_kobostation)))
//                }
//            }
//        }
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(currentLatLng, 14.5F),
                object : GoogleMap.CancelableCallback {
                    override fun onFinish() {
                        overviewBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                    }

                    override fun onCancel() {}
                })
        var circleOptions = CircleOptions()
                .center(currentLatLng).radius(1000.0)
                .fillColor(Color.parseColor("#5029489b"))
                .strokeColor(Color.parseColor("#29489b"))
                .strokeWidth(1F)
        mMap.addCircle(circleOptions)

        val marker = mMap.addMarker(
                MarkerOptions()
                        .position(currentLatLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location)))


    }


    private fun bootstrapAvailableTrucksOnMap(truckData: TruckData) {

        trkData = truckData

        clearMapAndData(ClearCommand.ALL)
        btnSearchButton.revertAnimation()
        btnCloseTruckInfo.visibility = View.VISIBLE
        searchForAvailableTrucks = true
        searchBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        overviewBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        if ((truckData.radiusInKM != null) && truckData.type == TruckDataConst.TYPE_RADIUS) {
            val circleOptions = CircleOptions()
                    .center(globalSelectedAdd.pickUp?.latLng).radius(1000.0)
                    .fillColor(Color.parseColor("#5029489b"))
                    .strokeColor(Color.parseColor("#29489b"))
                    .strokeWidth(1F)
            mMap.addCircle(circleOptions)

            val marker = mMap.addMarker(
                    globalSelectedAdd.pickUp?.latLng?.let {
                        MarkerOptions()
                                .position(it)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location))
                    })
        }

        if ((!truckData.polyline.isNullOrEmpty()) && truckData.type == TruckDataConst.TYPE_DESTINATION) {
            drawPolyLine(mapService.decodePoly(truckData.polyline))
        }


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(globalSelectedAdd.pickUp?.latLng, 14.5f))
        truckData.trucks.forEach { model -> addTrucks(model) }
        setupClusterManager(truckData.trucks)

    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onSwitchToMapClickListener() {
        switchToList.visibility = View.VISIBLE
        val fragment: Fragment? = supportFragmentManager.findFragmentByTag("fragmentSearchAvailableTrucks")
        if (fragment != null)
            supportFragmentManager.beginTransaction().remove(fragment).commit()
    }

    override fun onSelect(selectedTruckInfo: Trucks) {

        try {
            clearMapAndData(ClearCommand.MAP)
            selectedTruck = selectedTruckInfo
            selectedTruck?.lastKnownLocation?.let {
                val marker = mMap.addMarker(
                        selectedTruck?.bearing?.toFloat()?.let { it1 ->
                            NewBaseMapActivity.toLatLng(it.coordinates)?.let { it2 ->
                                MarkerOptions()
                                        .position(it2)
                                        //                                                .title(selectedTruck!!.d.reg_number)
                                        .rotation(it1)
                                        .icon(NewBaseMapActivity.truckFromStatus(selectedTruck!!))
                            }
                        }
                )

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(NewBaseMapActivity.toLatLng(it.coordinates), 14.5f))
            }

            truckDetailsBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED

            switchToList.visibility = View.VISIBLE
            val fragment: Fragment? = supportFragmentManager.findFragmentByTag("fragmentSearchAvailableTrucks")
            if (fragment != null)
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            setTruckDetails()
            truckDetailsBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED

        } catch (e: Exception) {
            Timber.i(e.message.toString())
        }
    }

    override fun onTripInfoCloseButtonClick() {
        switchToList.visibility = View.VISIBLE
        val fragment: Fragment? = supportFragmentManager.findFragmentByTag("fragmentSearchAvailableTrucks")
        if (fragment != null)
            supportFragmentManager.beginTransaction().remove(fragment).commit()
    }
}
