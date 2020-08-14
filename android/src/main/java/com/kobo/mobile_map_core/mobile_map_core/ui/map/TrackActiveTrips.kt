package com.kobo.mobile_map_core.mobile_map_core.ui.map

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.LatLngBounds
import com.google.android.libraries.maps.model.MarkerOptions
//import com.google.android.libraries.maps.CameraUpdateFactory
//import com.google.android.libraries.maps.GoogleMap
//import com.google.android.libraries.maps.model.LatLngBounds
//import com.google.android.libraries.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kobo.mobile_map_core.mobile_map_core.MobileMapCorePlugin
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.animation.TruckMover
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiHelper
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiServiceImpl
import com.kobo.mobile_map_core.mobile_map_core.data.models.ClearCommand
import com.kobo.mobile_map_core.mobile_map_core.data.models.NavigationData
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.ActiveTripsData
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import com.kobo.mobile_map_core.mobile_map_core.enums.FocusFrom
import com.kobo.mobile_map_core.mobile_map_core.enums.MapDisplayMode
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.ActiveTripsDetailsPagerAdapter
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.ActiveTripsPagerAdapter
import com.kobo.mobile_map_core.mobile_map_core.ui.base.ViewModelFactory
import com.kobo.mobile_map_core.mobile_map_core.ui.fragments.FilteredActiveTrips
import com.kobo.mobile_map_core.mobile_map_core.ui.fragments.FragmentActiveTripEvents
import com.kobo.mobile_map_core.mobile_map_core.ui.fragments.FragmentTripDetails
import com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel.ActiveTripsViewModel
import com.xdev.mvvm.utils.Status
import kotlinx.android.synthetic.main.activity_track_active_trips.*
import java.util.*


class TrackActiveTrips : NewBaseMapActivity(), FilteredActiveTrips.OnTripInfoClickedListener, FilteredActiveTrips.OnTripInfoCloseButtonClickListener, FragmentTripDetails.OnStartNavigationClickListener, FilteredActiveTrips.SwitchToMapClickListener {

    private lateinit var adapterViewPager: ActiveTripsPagerAdapter
    private lateinit var activeTripDetailsAdapterViewPager: ActiveTripsDetailsPagerAdapter
    private lateinit var vpPager: ViewPager
    private lateinit var vpPagerActiveTripDetails: ViewPager
    private lateinit var toggle: RadioGroup
    private lateinit var toggleDetails: RadioGroup
    private lateinit var getActiveTripsViewModel: ActiveTripsViewModel
    private lateinit var tripInfoBottomSheet: BottomSheetBehavior<View>
    private lateinit var bottomSheetView: View
    private lateinit var activeTripsData: ActiveTripsData


    val TRIP_INFO = "trip_info"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Pass context
        setContentView(R.layout.activity_track_active_trips)
        context = this@TrackActiveTrips
        shaper = this.let {
            PreferenceManager.getDefaultSharedPreferences(it)
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toggle = findViewById(R.id.toggle)
        toggleDetails = findViewById(R.id.toggleDetails)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.elevation = 0f
        actionBar.setDisplayShowHomeEnabled(true)

        vpPager = findViewById<View>(R.id.vpPager) as ViewPager
        vpPagerActiveTripDetails = findViewById<View>(R.id.vpPagerActiveTripDetails) as ViewPager
        bottomSheetView = findViewById(R.id.tripDetailsBottomSheet)
        bottomSheetView.visibility = View.GONE
        tripInfoBottomSheet = BottomSheetBehavior.from(bottomSheetView)
        tripInfoBottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
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
        vpPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0)
                    toggle.check(R.id.setDisplayTripsByLocation)
                else
                    toggle.check(R.id.setDisplayTripsInTraffic)

            }
        })


        adapterViewPager = ActiveTripsPagerAdapter(supportFragmentManager)
        vpPager.adapter = adapterViewPager

        initMapStuffs()
        setupViewModel()
        setupObserver()
        initActiveTrips()


    }


    private fun initActiveTrips() {
        val userTypeAndId = shaper.getString(MobileMapCorePlugin.KEY_USER_TYPE_AND_ID, "")
        userTypeAndId?.let { getActiveTripsViewModel.fetchActiveTrips(userTypeAndId) }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun displayTripsInTraffic(view: View) {
        vpPager.currentItem = 1
    }

    fun displayTripsByLocation(view: View) {
        vpPager.currentItem = 0
    }

    fun displayTripsDetails(view: View) {
        vpPagerActiveTripDetails.currentItem = 0
    }

    fun displayTripsEvents(view: View) {
        vpPagerActiveTripDetails.currentItem = 1
    }

    private fun setupViewModel() {
        getActiveTripsViewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiServiceImpl(this)))
        ).get(ActiveTripsViewModel::class.java)
    }

    private fun setupObserver() {
        getActiveTripsViewModel.getActiveTripsData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    pBar.visibility = View.GONE
//                    progressBar.visibility = View.GONE
//                    listShimmerActiveTrips.hideShimmer()
                    it.data?.let { activeTripsData -> updateUiAnalytics(activeTripsData.data) }!!
//                    recyclerView.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    pBar.visibility = View.VISIBLE
//                    progressBar.visibility = View.VISIBLE
//                    recyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
                    pBar.visibility = View.GONE
                    //Handle Error
//                    progressBar.visibility = View.GONE
//                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun updateUiAnalytics(data: ActiveTripsData) {
        activeTripsData = data
        tvTotalActiveTrips.text = data.tripsData.total.toString()
        tvEnroutePickUp.text = data.tripsData.analytics?.toPickup.toString()
        tvEnrouteDelivery.text = data.tripsData.analytics?.toDelivery.toString()
        tvWaitingAtDestination.text = data.tripsData.analytics?.atDestination.toString()
        tvStopped.text = data.tripsData.analytics?.stopped.toString()
        tvDiverted.text = data.tripsData.analytics?.diverted.toString()

    }

    fun openFragmentFilteredActiveTrips(view: View) {
        openFilteredActiveTripsFragment(null)
    }

    fun switchToListFromMap(view: View) {

        bottomSheetView.visibility = View.GONE
        val count = supportFragmentManager.backStackEntryCount
        if (count > 0) {
            supportFragmentManager.popBackStack()
        }
        openFilteredActiveTripsFragment(null)

    }

    fun openMapFragmentWithAllTrucks(view: View) {
        if (pBar.visibility == View.GONE) {
            switchToList.visibility = View.VISIBLE
            displayMode = MapDisplayMode.MultipleTruckClusteringMode
            openMapFragment()
        }

    }

    fun openFragmentFilteredToPickUp(view: View) {
        openFilteredActiveTripsFragment("toPickup")
    }

    fun openFragmentFilteredToDelivery(view: View) {
        openFilteredActiveTripsFragment("toDelivery")
    }

    fun openFragmentFilteredAtDestination(view: View) {
        openFilteredActiveTripsFragment("atDestination")
    }

    fun openFragmentFilteredStopped(view: View) {
        openFilteredActiveTripsFragment("stopped")
    }

    fun openFragmentFilteredDiverted(view: View) {
        openFilteredActiveTripsFragment("diverted")
    }


    private fun openFilteredActiveTripsFragment(filterBy: String?) {
        switchToList.visibility = View.GONE
        val filteredActiveTrips = FilteredActiveTrips.newInstance(filterBy)
        filteredActiveTrips.setOnTripInfoClickedListener(this)
        filteredActiveTrips.setOnTripInfoCloseButtonClickListener(this)
        filteredActiveTrips.setSwitchToMapClickListener(this)
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.mainActiveTripsHome, filteredActiveTrips, "filteredActiveTrips").addToBackStack("filteredActiveTrips").commit()

    }


    override fun onBackPressed() {
        if (tripInfoBottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED ||
                tripInfoBottomSheet.state == BottomSheetBehavior.STATE_HIDDEN ||
                bottomSheetView.visibility == View.GONE || bottomSheetView.visibility == View.INVISIBLE) {

            when (focusFrom) {
                FocusFrom.Map -> {
                    focusFrom = FocusFrom.Default
                    tripInfoBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                    multipleTruckClusteringMode()
                }
                else -> {
                    bottomSheetView.visibility = View.GONE
                    val count = supportFragmentManager.backStackEntryCount
                    if (count == 0) {
                        super.onBackPressed()
                    } else {
                        supportFragmentManager.popBackStack()
                    }
                }
            }
            truckMover?.stopTruckMovementTimer()
            truckMover = null
            keepListening = false
            try {
//                unSubscribe(mqttTopic)
//                mqttAndroidClient?.disconnect()
            } catch (e: Exception) {
            }


        } else {

            tripInfoBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }

//        val fragment: Fragment? = supportFragmentManager.findFragmentByTag("filteredActiveTrips")
//        if (fragment != null)
//            supportFragmentManager.beginTransaction().remove(fragment).commit()
//        else
//            super.onBackPressed();

    }

    override fun onSelect(selectedTripInfo: Trucks) {

        truckInfo = selectedTripInfo
        displayMode = MapDisplayMode.SingleTripTruckFocusMode
        focusFrom = FocusFrom.List
        openMapFragment()
        setUpTripInfoViewPager()

    }

    override fun onTripInfoCloseButtonClick() {
        onBackPressed()
    }

    fun setUpTripInfoViewPager() {


        val fragmentTripDetails: FragmentTripDetails = FragmentTripDetails.newInstance(truckInfo)
        fragmentTripDetails.setOnStartNavigationClickListener(this)
        val fragmentActiveTripEvents: FragmentActiveTripEvents = FragmentActiveTripEvents.newInstance(truckInfo)

        val pages: MutableList<Fragment> = arrayListOf()
        pages.add(fragmentTripDetails)
        pages.add(fragmentActiveTripEvents)

        activeTripDetailsAdapterViewPager = ActiveTripsDetailsPagerAdapter(supportFragmentManager, pages)
        vpPagerActiveTripDetails.adapter = activeTripDetailsAdapterViewPager
        vpPagerActiveTripDetails.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0)
                    toggleDetails.check(R.id.setDisplayTripDetails)
                else
                    toggleDetails.check(R.id.setDisplayTripEvents)

            }
        })
        bottomSheetView.visibility = View.VISIBLE
        tripInfoBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED

    }

    override fun oyaStart(selectedTripInfo: Trucks?) {
        val intent = Intent(this, NavigationActivity::class.java)
        val navigationData = NavigationData(regNumber = truckInfo.regNumber, tripId = truckInfo.tripDetail?.tripId, tripReadId = truckInfo.tripDetail?.tripReadId, destination = truckInfo.tripDetail?.deliveryLocation?.coordinates)
        intent.putExtra(NavigationActivity.NAVIGATION_DATA, navigationData)
        startActivity(intent)
    }


    override fun multipleTruckClusteringMode() {
        clearMapAndData(ClearCommand.ALL)
        tripMarkerManager = activeTripsData.tripsData.trucks?.map { it?.regNumber!! to it }?.toMap()?.toMutableMap()
//        val convertTripsToTruckList: List<Trucks?>? = activeTripsData.tripsData.trucks
//        convertTripsToTruckList?.let { setupClusterManager(it) }
        activeTripsData.tripsData.trucks?.let { setupClusterManager(it) }

    }

    override fun setUpClusterManagerClickListener() {
        clusterManager.setOnClusterClickListener {

            val items = it.items
            val builder = LatLngBounds.Builder()
            for (item in items) {
                builder.include(item.position)
            }
            val bounds = builder.build()
            val mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
            mMap.animateCamera(mCameraUpdate)
            true
        }
        clusterManager.setOnClusterItemClickListener {
            mMap.animateCamera(
                    CameraUpdateFactory.newLatLng(it.position),
                    object : GoogleMap.CancelableCallback {
                        override fun onFinish() {

                            focusFrom = FocusFrom.Map

                            clearMapAndData(ClearCommand.MAP)

                            truckInfo = tripMarkerManager?.get(it.title)!!
                            setUpTripInfoViewPager()
                            if (truckInfo.tripDetail?.travelledRoutePolyline?.isNotEmpty()!! && truckInfo.tripDetail?.currentBestRoute?.isNotEmpty()!!) {
                                drawPolyLine(travelledPolyList = mapService.decodePoly(truckInfo.tripDetail?.travelledRoutePolyline!!),
                                        currentBestPolyList = mapService.decodePoly(truckInfo.tripDetail?.currentBestRoute!!))
                            }


                            selectedMarker = mMap.addMarker(
                                    truckInfo.lastKnownLocation?.coordinates?.let { it1 -> toLatLng(it1) }?.let { it2 ->
                                        truckInfo.bearing?.toFloat()?.let { it1 ->
                                            MarkerOptions()
                                                    .position(it2)
                                                    //                                                .title(selectedTruck!!.d.reg_number)
                                                    .rotation(it1)
                                                    .icon(truckFromStatus(truckInfo))
                                        }
                                    }
                            )


                            mqttTopic = "client/track/${truckInfo.regNumber}".toLowerCase(Locale.getDefault())
                            setUpMQTT()
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(truckInfo.lastKnownLocation?.coordinates?.let { latLngFromList(it) }, 17f))
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
//                            truckInfo.locations?.let {
//                                TruckMover(mMap, context, selectedMarker).showMovingTruck(
//                                        ArrayList(it)
//                                )}


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
                        }

                        override fun onCancel() {}
                    })
            true
        }
    }

    override fun onSwitchToMapClickListener() {
        bottomSheetView.visibility = View.GONE
        val count = supportFragmentManager.backStackEntryCount
        if (count > 0) {
            supportFragmentManager.popBackStack()
        }
        if (pBar.visibility == View.GONE) {
            displayMode = MapDisplayMode.MultipleTruckClusteringMode
            switchToList.visibility = View.VISIBLE
            openMapFragment()
        }
    }


}

