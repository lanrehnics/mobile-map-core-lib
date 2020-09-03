package com.kobo.mobile_map_core.mobile_map_core.ui.map

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.ClusterManager
import com.kobo.mobile_map_core.mobile_map_core.MobileMapCorePlugin
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiHelper
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiServiceImpl
import com.kobo.mobile_map_core.mobile_map_core.data.models.ClearCommand
import com.kobo.mobile_map_core.mobile_map_core.data.models.PrepareFragmentModel
import com.kobo.mobile_map_core.mobile_map_core.data.models.SelectedAddrss
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Location
import com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model.AssetClassResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model.AssetClasses
import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.Autocomplete
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.TruckData
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.TruckDataConst
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview.Overview
import com.kobo.mobile_map_core.mobile_map_core.data.models.orders.AvailableOrdersData
import com.kobo.mobile_map_core.mobile_map_core.data.models.reverse_geocode.ReverseGeocodeResponse
import com.kobo.mobile_map_core.mobile_map_core.data.services.MapService
import com.kobo.mobile_map_core.mobile_map_core.ui.base.ViewModelFactory
import com.kobo.mobile_map_core.mobile_map_core.ui.fragments.FragmentSearchAvailableOrders
import com.kobo.mobile_map_core.mobile_map_core.ui.fragments.FragmentSearchAvailableTrucks
import com.kobo.mobile_map_core.mobile_map_core.ui.fragments.GeneralSearchFragment
import com.kobo.mobile_map_core.mobile_map_core.ui.fragments.SearchForPlaces
import com.kobo.mobile_map_core.mobile_map_core.ui.fragments.`interface`.UseFulFragmentsInterface
import com.kobo.mobile_map_core.mobile_map_core.ui.user_actions.CustomerActionsImpl
import com.kobo.mobile_map_core.mobile_map_core.ui.user_actions.PartnerActionImpl
import com.kobo.mobile_map_core.mobile_map_core.ui.user_actions.UserTypeAction
import com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel.MapLandingViewModel
import com.kobo.mobile_map_core.mobile_map_core.utils.GpsUtils
import com.xdev.mvvm.utils.Status
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.battlefield_landing_activity_main.*
import kotlinx.android.synthetic.main.fragment_user_options.*
import kotlinx.android.synthetic.main.search_available_order.*
import kotlinx.android.synthetic.main.search_available_truck.*
import timber.log.Timber
import java.util.*


class BattlefieldLandingActivity : BaseMapActivity(), OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, View.OnClickListener, SearchForPlaces.OnAddressSelectedListener, UseFulFragmentsInterface.SwitchToMapClickListener, UseFulFragmentsInterface.OnInfoClickedListener, UseFulFragmentsInterface.OnCloseButtonClickListener, GoogleMap.OnMyLocationButtonClickListener, GeneralSearchFragment.OnResultItemClickListener {

    private lateinit var mapLandingViewModel: MapLandingViewModel
    private lateinit var assetClasses: AssetClasses

    //    private lateinit var selectedSize: Size
    private var searchRadius: Int? = 1
    private var searchForAvailableTrucks: Boolean = false
    private lateinit var data: Any
    private var overviewW: Overview? = null
    private lateinit var userTypeAction: UserTypeAction
    private lateinit var searchCard: CardView
    private lateinit var clearSearch: ImageView

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        shaper = this.let {
            PreferenceManager.getDefaultSharedPreferences(it)
        }


        //Initialize views and click listeners
        initializeViews()
        setupViewModel()
        setupFetchGroupedAssetObserver()


        // Initialize map, services, bottom sheet and drawer
        mapService = MapService(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mMapView = mapFragment.requireView()
        mapFragment.getMapAsync(this)

        overviewBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        searchBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        searchBottomSheetOrder.state = BottomSheetBehavior.STATE_HIDDEN
        truckDetailsBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        availableOrderBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN


    }

    private fun setUpSpinner(res: AssetClassResponse) {
        userTypeAction.setUpSpinner(this, res.data.assetClasses) { result ->
            setAssetClasses(result)
        }
    }

    private fun setAssetClasses(result: AssetClasses) {
        assetClasses = result
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
        mapLandingViewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiServiceImpl(this)))
        ).get(MapLandingViewModel::class.java)
    }


    private fun setupFetchGroupedAssetObserver() {
        mapLandingViewModel.getGroupedAssetClass().observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { response -> setUpSpinner(response) }
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

                    it.data?.let { response -> setMyCurrentLocations(response) }
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

    private fun setMyCurrentLocations(res: ReverseGeocodeResponse) {
//        Log.i(BattlefieldLandingActivity::class.java.name, ">>>>> ${res.data.geocoded.location.address}")
        val address = res.data.geocoded.location.address
        globalSelectedAdd.pickUp?.description = address
        tvMyCurrentLocation.text = address

        userTypeAction.setEtPickUp(address.toEditable())
    }


    override fun onMapReady(googleMap: GoogleMap) {


        mMap = googleMap
        mMap.setOnMapLongClickListener {
            currentLatLng = it
            fetchAndSubscribeForLocationOverview()
        }
        clusterManager = ClusterManager(this, googleMap)
        ordersClusterManager = ClusterManager(this, googleMap)


        userTypeAction.setMap(mMap, clusterManager, ordersClusterManager)
        setClusterManagerClickListener()
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.isIndoorEnabled = false
        mMap.isBuildingsEnabled = true
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

        GpsUtils(this).turnGPSOn {
            if (it) {
                setUpLocationPermission()
            }
        }
        searchCard.visibility = View.VISIBLE

    }

    override fun setUpClusterManagerClickListener() {
    }

    override fun multipleTruckClusteringMode() {
    }

    override fun setUpLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)

            return
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                // 3
                if (location != null) {

                    mMap.isMyLocationEnabled = true
                    mMap.setOnMyLocationButtonClickListener(this)
//                lastLocation = location
                    currentLatLng = LatLng(location.latitude, location.longitude)

                    globalSelectedAdd.pickUp?.latLng = currentLatLng
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))

                    subscribeForReverseGeocode()
                    fetchAndSubscribeForLocationOverview()
                }
            }

            val locationButton = (mMapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(Integer.parseInt("2"))
//
            val rlp = locationButton.layoutParams as (RelativeLayout.LayoutParams)
            // position on right bottom
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
            rlp.setMargins(0, 30, 60, 30)
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
                    it.data?.let { response -> bootstrapLocationOverview(response.data?.overview) }
                }
                Status.LOADING -> {
                    pBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    pBar.visibility = View.INVISIBLE
                    //Handle Error
                }
            }
        })

    }

    private fun bookTruck() {

        val truck: Trucks = selectedItem as Trucks;

        mapLandingViewModel.bookTruck(truck.regNumber).observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    btnBookTruck.stopAnimation()
                    btnBookTruck.revertAnimation {
//                        progressButton.text = "Some new text"
                    }
                    it.data?.let { data -> showSuccessMessage(findViewById(R.id.mainMapHome), "Truck Booked") }

                }
                Status.LOADING -> {
                    btnBookTruck.startAnimation()
                }
                Status.ERROR -> {
                    btnBookTruck.stopAnimation()
                    btnBookTruck.revertAnimation {
//                        progressButton.text = "Some new text"
                    }
                    it.message?.let { errorMessage ->
                        showErrorMessage(findViewById(R.id.mainMapHome), errorMessage)
                    }
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

            R.id.btnSearchButtonOrder -> {
                fetchAvailableOrder(globalSelectedAdd.pickUp?.latLng, assetClasses.type)
            }

            R.id.btnBookTruck -> {

                val dialogBuilder = AlertDialog.Builder(this)

                dialogBuilder.setMessage("Do you want to continue?")
                        .setCancelable(false)
                        .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, id ->
                            dialog.dismiss()
                            bookTruck()
                        })
                        .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                            dialog.cancel()
                        })

                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle("Book truck")
                // show alert dialog
                alert.show()
            }

            R.id.btnCloseTruckInfo -> {
                searchForAvailableTrucks = false
                userTypeAction.setSearchForAvailableTrucks(searchForAvailableTrucks)
                btnCloseTruckInfo.visibility = View.INVISIBLE
                searchCard.visibility = View.VISIBLE
                userTypeAction.expandSearchBottomSheet()
                if (
                        truckDetailsBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED ||
                        availableOrderBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED


                ) {
                    truckDetailsBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                    availableOrderBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
//                    overviewBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                fetchAndSubscribeForLocationOverview()
            }
            else -> {
//                ("Button Not Handled")
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray) {
        Timber.i("onRequestPermissionResult")
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> Timber.i("User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                        if (location != null) {
                            val currentLatLng = LatLng(location.latitude, location.longitude)
                            mMap.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                            currentLatLng,
                                            12f
                                    )
                            )

                            onMapReady(mMap)

                        }
                    }
                }
                else -> Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setClusterManagerClickListener() {
        userTypeAction.setClusterManagerClickListener { result -> clusterClickListener(result) }
    }

    private fun clusterClickListener(result: String?) {
        result?.let {
            val resultList = it.split(",")
            try {

                selectedItem = itemMarkerManager[resultList[0]]!!
                setItemDetails(resultList[1])

            } catch (e: Exception) {
                Timber.i(e.message.toString())
            }
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
        searchCard = findViewById(R.id.searchCard)
        clearSearch = findViewById(R.id.clearSearch)
        clearSearch.setOnClickListener {
            fetchAndSubscribeForLocationOverview()
            searchCard.visibility = View.VISIBLE
            clearSearch.visibility = View.GONE
        }
        searchCard.visibility = View.GONE
        clearSearch.visibility = View.GONE
        btnSearchButton = findViewById(R.id.btnSearchButton)
        btnBookTruck = findViewById(R.id.btnBookTruck)
        btnSearchButtonOrder = findViewById(R.id.btnSearchButtonOrder)


        overviewBottomSheet = BottomSheetBehavior.from(findViewById(R.id.layout_bottom_sheet))
        searchBottomSheet = BottomSheetBehavior.from(findViewById(R.id.searchAvailableTruckId))
        searchBottomSheetOrder = BottomSheetBehavior.from(findViewById(R.id.searchAvailableOrderId))
        truckDetailsBottomSheet = BottomSheetBehavior.from(findViewById(R.id.dedicatedTruckBottomSheet))
        availableOrderBottomSheet = BottomSheetBehavior.from(findViewById(R.id.availableOrderBottomSheet))

        bottomSheetHeader.setOnClickListener(this)
        btnSearchButton.setOnClickListener(this)
        btnBookTruck.setOnClickListener(this)
        btnSearchButtonOrder.setOnClickListener(this)
        btnCloseTruckInfo.setOnClickListener(this)

        when (shaper.getString(MobileMapCorePlugin.KEY_APP_TYPE, "")) {
            MobileMapCorePlugin.APP_TYPE_CUSTOMER -> {
                userTypeAction = CustomerActionsImpl(
                        overviewBottomSheet,
                        searchBottomSheet,
                        truckDetailsBottomSheet,
                        spinner,
                        etPickUpLocation)
            }

            MobileMapCorePlugin.APP_TYPE_TRANSPORTER -> {
                userTypeAction = PartnerActionImpl(
                        overviewBottomSheet,
                        searchBottomSheetOrder,
                        availableOrderBottomSheet,
                        spinnerOrder,
                        etPickUpLocationOrder)
            }

            MobileMapCorePlugin.APP_TYPE_PARTNER -> {
                userTypeAction = PartnerActionImpl(
                        overviewBottomSheet,
                        searchBottomSheetOrder,
                        availableOrderBottomSheet,
                        spinnerOrder,
                        etPickUpLocationOrder)
            }

            MobileMapCorePlugin.APP_TYPE_SQUAD -> {

            }

            MobileMapCorePlugin.APP_TYPE_DRIVER -> {

            }
        }
    }

    fun openSearchAvailableTrucks(view: View) {
        userTypeAction.openSearchAvailableTrucks()
    }

    fun openSearchFragment(view: View) {
        val generalSearch = GeneralSearchFragment()
        generalSearch.setOnResultItemClickListener(this)
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.mainMapHome, generalSearch, "GeneralSearchFragment").addToBackStack("GeneralSearchFragment").commit()
    }

    fun closeSearchBottomSheet(view: View) {
        userTypeAction.closeSearchBottomSheet()
    }


    fun openTrackActiveTrips(view: View) {
        startActivity(Intent(this, TrackActiveTrips::class.java))
    }

    fun openDedicatedTruck(view: View) {
        startActivity(Intent(this, DedicatedTruckActivity::class.java))
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

        userTypeAction.switchToListFromMap(data) { prepareFragment -> prepareListFragment(prepareFragment) }


    }

    private fun prepareListFragment(prepareFragment: PrepareFragmentModel) {

        when (prepareFragment.displayFor) {
            "order" -> {

                val fragment: FragmentSearchAvailableOrders = prepareFragment.fragment as FragmentSearchAvailableOrders

                fragment.setSwitchToMapClickListener(this)
                fragment.setOnTruckInfoClickedListener(this)
                fragment.setOnAvailableTruckCloseButtonClickListener(this)
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.mainMapHome, fragment, "fragmentSearchAvailableOrders").addToBackStack("fragmentSearchAvailableOrders").commit()
            }

            else -> {


                val fragment: FragmentSearchAvailableTrucks = prepareFragment.fragment as FragmentSearchAvailableTrucks

                fragment.setSwitchToMapClickListener(this)
                fragment.setOnTruckInfoClickedListener(this)
                fragment.setOnAvailableTruckCloseButtonClickListener(this)
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.mainMapHome, fragment, "fragmentSearchAvailableTrucks").addToBackStack("fragmentSearchAvailableTrucks").commit()

            }
        }
    }


    override fun onBackPressed() {
        if (
                truckDetailsBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED ||
                availableOrderBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED
        ) {
            truckDetailsBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            availableOrderBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        } else if (searchForAvailableTrucks) {

            val count = supportFragmentManager.backStackEntryCount
            if (count > 0) {
                supportFragmentManager.popBackStack()
            } else {
                fetchAndSubscribeForLocationOverview()
                searchForAvailableTrucks = false
                userTypeAction.setSearchForAvailableTrucks(searchForAvailableTrucks)
                btnCloseTruckInfo.visibility = View.INVISIBLE
                userTypeAction.expandSearchBottomSheet()
            }
        } else {
            super.onBackPressed()
        }
    }


    private fun bootstrapLatLng(placesId: String, add: Autocomplete?) {
        mapLandingViewModel.fetchLatLngFromPlacesId(placesId).observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { response -> setLatLngInfo(response.data.place.geometry.location, add) }
                }
                Status.LOADING -> {
//                    progressBar.visibility = View.VISIBLE
//                    recyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
//                    println("")
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
                    it.data?.let { response -> bootstrapAvailableTrucksOnMap(response.data.truckData) }
                }

                Status.LOADING -> {
                    btnSearchButton.startAnimation()
//                    progressBar.visibility = View.VISIBLE
//                    recyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
                    btnSearchButton.revertAnimation()
//                    println("Error")
                    //Handle Error
//                    progressBar.visibility = View.GONE
//                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })

    }


    private fun fetchAvailableOrder(origin: LatLng?, assetType: String) {
        mapLandingViewModel.fetchAvailableOrder(origin, assetType).observe(this, androidx.lifecycle.Observer {
            when (it.status) {

                Status.SUCCESS -> {
                    switchToList.visibility = View.VISIBLE
                    searchCard.visibility = View.GONE
                    it.data?.let { response -> bootstrapAvailableOrdersOnMap(response.data) }
                }

                Status.LOADING -> {
                    btnSearchButtonOrder.startAnimation()
//                    progressBar.visibility = View.VISIBLE
//                    recyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
                    btnSearchButtonOrder.revertAnimation()
//                    println("Error")
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

        overview?.kobocareStations?.let {
            it.forEach { cl ->
                cl?.location?.let { loc ->
                    mMap.addMarker(
                            MarkerOptions()
                                    .position(NewBaseMapActivity.toLatLngNotNull(loc.coordinates))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_kobostation)))
                }
            }
        }
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(currentLatLng, 13.0F),
                object : GoogleMap.CancelableCallback {
                    override fun onFinish() {
                        overviewBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                    }

                    override fun onCancel() {}
                })
        var circleOptions = CircleOptions()
                .center(currentLatLng).radius(3000.0)
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
        data = truckData

        clearMapAndData(ClearCommand.ALL)
        btnSearchButton.revertAnimation()
        btnCloseTruckInfo.visibility = View.VISIBLE
        searchForAvailableTrucks = true
        userTypeAction.setSearchForAvailableTrucks(searchForAvailableTrucks)
        searchBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        overviewBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        if ((truckData.radiusInKM != null) && truckData.type == TruckDataConst.TYPE_RADIUS) {
            val circleOptions = CircleOptions()
                    .center(globalSelectedAdd.pickUp?.latLng).radius((truckData.radiusInKM * 1000.0))
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
        truckData.trucks?.forEach { model -> addTrucks(model) }
        truckData.trucks?.let { setupClusterManager(it) }
        searchCard.visibility = View.GONE


    }

    private fun bootstrapAvailableOrdersOnMap(availableOrdersData: AvailableOrdersData) {

        data = availableOrdersData

        clearMapAndData(ClearCommand.ALL)
        btnSearchButtonOrder.revertAnimation()
        searchCard.visibility = View.GONE
        btnCloseTruckInfo.visibility = View.VISIBLE
        searchForAvailableTrucks = true
        userTypeAction.setSearchForAvailableTrucks(searchForAvailableTrucks)
        searchBottomSheetOrder.state = BottomSheetBehavior.STATE_HIDDEN
        overviewBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(globalSelectedAdd.pickUp?.latLng, 14.5f))
        availableOrdersData.orders?.forEach { model -> addOrders(model) }
        availableOrdersData.orders?.let { setupOrderClusterManager(it) }

    }

    override fun onSelect(selectedAddress: SelectedAddrss) {
        when (selectedAddress.mode) {
            0 -> {
                globalSelectedAdd.pickUp = selectedAddress.pickUp
                userTypeAction.setEtPickUp(selectedAddress.pickUp?.description?.toEditable())
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

    override fun onSwitchToMapClickListener() {
        switchToList.visibility = View.VISIBLE
        userTypeAction.onSwitchToMapClickListener(supportFragmentManager)
    }

    override fun onSelect(selectedInfo: Any) {
        try {
            selectedItem = selectedInfo
            switchToList.visibility = View.VISIBLE
            clearMapAndData(ClearCommand.MAP)
            userTypeAction.onSelectFromList(selectedInfo, supportFragmentManager) { displayFor -> setItemDetails(displayFor) }

        } catch (e: Exception) {
            Timber.i(e.message.toString())
        }
    }

    override fun onTripInfoCloseButtonClick() {
        switchToList.visibility = View.VISIBLE
        userTypeAction.onTripInfoCloseButtonClick(supportFragmentManager)
    }

    private fun String?.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    override fun onMyLocationButtonClick(): Boolean {
        GpsUtils(this).turnGPSOn {
            if (it) {
                setUpLocationPermission()
            }
        }
        return true
    }

    override fun onSearchItemResultSelected(truck: Trucks) {
        val count = supportFragmentManager.backStackEntryCount
        if (count > 0) {
            supportFragmentManager.popBackStack()
        }
        toLatLng(truck.lastKnownLocation?.coordinates)?.let {
            mMap.animateCamera(
                    CameraUpdateFactory.newLatLng(it),
                    object : GoogleMap.CancelableCallback {
                        override fun onFinish() {

                            if (truck.bearing == null) {
                                showErrorMessage(findViewById(R.id.mainMapHome), "No bearing for truck")
                            } else {
                                clearMapAndData(ClearCommand.MAP)

                                selectedMarker = mMap.addMarker(
                                        MarkerOptions()
                                                .position(it)
                                                .rotation(truck.bearing.toFloat())
                                                .icon(truckFromStatus(truck))
                                )

                                searchCard.visibility = View.GONE
                                clearSearch.visibility = View.VISIBLE
                                truckDetailsBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                                availableOrderBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                                mqttTopic = "client/track/${truck.regNumber}".toLowerCase(Locale.getDefault())
                                setUpMQTT()
                                truck.events?.let { events ->
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


                            if (truck.tripDetail != null) {
                                if (truck.tripDetail.travelledRoutePolyline.isNotEmpty() && truck.tripDetail.currentBestRoute.isNotEmpty()) {
                                    drawPolyLine(travelledPolyList = mapService.decodePoly(truck.tripDetail.travelledRoutePolyline),
                                            currentBestPolyList = mapService.decodePoly(truck.tripDetail.currentBestRoute))
                                }

                            }


                        }

                        override fun onCancel() {}
                    })
        }
    }
}
