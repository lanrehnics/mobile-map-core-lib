package com.kobo.mobile_map_core.mobile_map_core.ui.map

import SelectedAddrss
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.libraries.maps.CameraUpdate
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.ClusterManager
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.enums.KoboLocationType
import com.kobo.mobile_map_core.mobile_map_core.data.models.*
import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.Autocomplete
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import com.kobo360.map.MarkerClusterRenderer
import com.kobo.mobile_map_core.mobile_map_core.data.services.MapService
import kotlinx.android.synthetic.main.dedicated_truck_bottom_sheet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

abstract class BaseMapActivity : AppCompatActivity() {

    //Private properties
    var globalSelectedAdd: SelectedAddrss = SelectedAddrss(
            Autocomplete(description = "", placeId = "", terms = null, latLng = LatLng(0.0, 0.0)),
            Autocomplete(description = "", placeId = "", terms = null, latLng = LatLng(0.0, 0.0)))
    val fragmentManager: FragmentManager = supportFragmentManager
    private lateinit var polylineOptions: PolylineOptions
    private lateinit var blackPolylineOptions: PolylineOptions
    private lateinit var blackPolyline: Polyline
    private lateinit var greyPolyLine: Polyline

    //Protected variables ...
    protected lateinit var context: Context
    protected var isMappedFiltered = false
    protected var isMappedFilteredByCustomer = false
    protected var isMappedFilteredByStations = false
    private var customersLocations: KoboLocations? = null
    protected var customerLocationList: MutableList<Locations?>? = arrayListOf()
    private var koboStationLocations: KoboLocations? = null
    protected var koboStationLocationsList: MutableList<Locations?>? = arrayListOf()
    protected lateinit var fusedLocationClient: FusedLocationProviderClient
    protected lateinit var mapService: MapService
    protected lateinit var mMap: GoogleMap
    protected lateinit var mMapView: View
    protected lateinit var overviewBottomSheet: BottomSheetBehavior<View>
    protected lateinit var searchBottomSheet: BottomSheetBehavior<View>
    protected lateinit var truckDetailsBottomSheet: BottomSheetBehavior<View>

    //    protected lateinit var fabMyLocation: View
//    protected lateinit var fabTripFilter: View
    //    protected lateinit var fabOpenSearch: View
    protected lateinit var btnSearchButton: CircularProgressButton

    //    protected lateinit var backArrowButton: ImageButton
    protected lateinit var toolbar: Toolbar
    protected lateinit var currentLatLng: LatLng
    protected lateinit var bottomSheetHeader: RelativeLayout

    //    protected lateinit var tvTripId: TextView
//    protected lateinit var tvTripStatus: TextView
//    protected lateinit var tvCustName: TextView
//    protected lateinit var tvPickUp: TextView
//    protected lateinit var tvDestination: TextView
//    protected lateinit var tvDriverName: TextView
//    protected lateinit var tvDriverMobile: TextView
//    protected lateinit var tvRegNumber: TextView
//    protected lateinit var tvEtt: TextView
    protected var isSearchOn = false
    protected lateinit var clusterManager: ClusterManager<TruckClusterItem>
    protected var markerManager: MutableMap<String, Marker> = mutableMapOf()


    protected lateinit var tripFilterLinearLayout: LinearLayout

    private var parserCounter = 0

    protected var truckMarkerManager: MutableMap<String, Trucks> = mutableMapOf()
    var pickUpStations: MutableMap<Marker, Locations> = mutableMapOf()
    var selectedTruck: Trucks? = null

    //    protected val listTruckModel: MutableList<TruckModelDataParser?> = ArrayList()
    protected val listFilterTerms: MutableList<String?> = ArrayList()

    protected lateinit var selectedMarker: Marker
    protected var currentDisplayMode: DisplayMode = DisplayMode.ALL

    protected fun showMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }


//    private fun focusOnTruck(truckId: String) {
//        backArrowButton.visibility = View.INVISIBLE
//        btnCloseTruckInfo.visibility = View.VISIBLE
//
//        val docRef = db.collection("com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks").document(truckId)
//        fireStoreSearchEventListener = docRef.addSnapshotListener { snapshot, e ->
//            if (e != null) {
//                Log.w(MapsActivity.TAG, "Listen failed.", e)
//                return@addSnapshotListener
//            }
//            if (snapshot != null && snapshot.exists()) {
//
//                fireStoreSearchEventListener?.remove()
//                Log.d(MapsActivity.TAG, "Current data: ${snapshot.data}")
//                val searchResult = snapshot.toObject(TruckModelDataParser::class.java)
//
//                if (searchResult != null) {
//                    isSearchOn = true
//                    mMap.uiSettings.isRotateGesturesEnabled = true
//                    mMap.addMarker(
//                            MarkerOptions()
//                                    .position(toLatLng(searchResult.l))
//                                    .title(searchResult.d.reg_number)
//                                    .rotation(searchResult.d.bearing.toFloat())
//                                    .icon(truckFromStatus(searchResult))
//                    )
//                    mMap.animateCamera(
//                            CameraUpdateFactory.newCameraPosition(
//                                    CameraPosition.Builder().target(
//                                            LatLng(searchResult.l.latitude, searchResult.l.longitude)
//                                    )
//                                            .zoom(16.5f).build()
//                            )
//                    )
//                }
//            } else {
//                Log.d(MapsActivity.TAG, "Current data: null")
//            }
//        }
//
//    }


    protected fun clearMapAndData(command: ClearCommand) {

        clusterManager.clearItems()
        customerLocationList?.clear()
        koboStationLocationsList?.clear()

        when (command) {
            ClearCommand.MAP -> {
                mMap.clear()
//                listTripStatusFilter.clear()
            }
            ClearCommand.DATA -> {
                truckMarkerManager.clear()
                markerManager.clear()
                listFilterTerms.clear()
            }
            ClearCommand.WITHOUT_VIEW -> {
                mMap.clear()
                truckMarkerManager.clear()
                markerManager.clear()
            }
            else -> {
                mMap.clear()
                markerManager.clear()
                truckMarkerManager.clear()
//                backArrowButton.visibility = View.VISIBLE
//                btnCloseTruckInfo.visibility = View.INVISIBLE
                listFilterTerms.clear()
            }
        }
    }

    protected fun resetView() {
        try {
            selectedTruck = null
            currentDisplayMode = DisplayMode.ALL
//            btnCloseTruckInfo.visibility = View.INVISIBLE
            clearMapAndData(ClearCommand.ALL)

            bootStrapPickupStationsAndTrucks()
        } catch (e: Exception) {
        }
    }

    protected fun addTrucks(model: Trucks?) {
        try {
            model?.lastKnownLocation?.let {
                if (it.coordinates[0] != 0.0) {
                    truckMarkerManager[model.regNumber] = model
                }
            }
//            if (truckMarkerManager.size <= 50) {

//            }
        } catch (error: Exception) {
            error.message?.let { Log.d("partner", it) }
        }

    }

    private fun addStations(model: Locations?, locationType: KoboLocationType) {
        try {
            if (model != null && model.lat > 0.0 && model.long > 0.0) {
                val marker = mMap.addMarker(
                        when (locationType) {
                            KoboLocationType.CUSTOMER -> {
                                MarkerOptions().position(LatLng(model.lat, model.long))
                                        .icon(BitmapDescriptorFactory.fromResource(
                                                R.drawable.warehouse
                                        )).title(model.contact_name).snippet("Customer address: ${model.address}")
                            }
                            KoboLocationType.STATION -> {
                                MarkerOptions().position(LatLng(model.lat, model.long))
                                        .icon(BitmapDescriptorFactory.fromResource(
                                                R.drawable.customer
                                        )
                                        )
                                        .title(model.contact_name)
                                        .snippet("KoboStation address: ${model.address}")
                            }
                        }
                )
                pickUpStations[marker] = model
            }
        } catch (error: Exception) {
            Log.d("partner", error.message)
        }
    }

    protected fun setTruckDetails() {
        tvTruckRegNo.text = selectedTruck?.regNumber
        tvAssetInfo.text = "${selectedTruck?.assetClass?.type} ${selectedTruck?.assetClass?.size}${selectedTruck?.assetClass?.unit}"
        tvCurrentNavigation.text = selectedTruck?.lastKnownLocation?.address
        tvDriverName.text = selectedTruck?.driver?.firstName
        tvDriverRating.text = selectedTruck?.driver?.rating.toString()
        ratingBarDriverRating.rating = selectedTruck?.driver?.rating?.toFloat()!!

        this.let {
            Glide.with(it)
                    .load(selectedTruck?.driver?.image)
                    .placeholder(ColorDrawable(Color.BLACK))
                    .into(profile_image)
        };
//        tvTripStatus.text = selectedTruck?.d?.status
//        tvCustName.text = selectedTruck?.d?.customerName
//        tvPickUp.text = selectedTruck?.d?.pickUpStation?.address
//        tvDestination.text = selectedTruck?.d?.deliveryStation?.address
//        tvDriverName.text = selectedTruck?.d?.driverName
//        tvDriverMobile.text = selectedTruck?.d?.driverMobile
//        tvRegNumber.text = selectedTruck?.d?.reg_number
//        tvEtt.text = "O Hour(s)"
    }

    protected fun drawPolyLine(
            polyLineList: List<LatLng>
    ) {
        val builder = LatLngBounds.Builder()
        for (latLng in polyLineList) {
            builder.include(latLng)
        }
        val bounds = builder.build()
        val mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
        mMap.animateCamera(mCameraUpdate)

        blackPolylineOptions = PolylineOptions()
        blackPolylineOptions.width(10f)
        blackPolylineOptions.color(Color.BLACK)
        blackPolylineOptions.startCap(SquareCap())
        blackPolylineOptions.endCap(SquareCap())
        blackPolylineOptions.jointType(JointType.ROUND)
        blackPolylineOptions.addAll(polyLineList)
        blackPolyline = mMap.addPolyline(blackPolylineOptions)
    }

    protected fun bootStrapPickupStationsAndTrucks() {
        clearMapAndData(ClearCommand.ALL)
        GlobalScope.launch(Dispatchers.Main) {
            try {
//                customersLocations = mapService.fetchCustomersLocations(toGeoHash(currentLatLng))
                customerLocationList = customersLocations?.data?.locations?.toMutableList()
//                koboStationLocations = mapService.fetchKoboStations(toGeoHash(currentLatLng))
                koboStationLocationsList = koboStationLocations?.data?.locations?.toMutableList()

//                customerLocationList?.forEach { model -> addTrucks(model?.let { TruckModelDataParser(it, "customer") }) }
//                koboStationLocationsList?.forEach { model -> addTrucks(model?.let { TruckModelDataParser(it, "station") }) }

//                runGeoQuery(verifyCurrentLocation(toGeoPoint(currentLatLng)))
            } catch (e: Exception) {
                Log.e(BattlefieldLandingActivity.TAG, "Error fetching customers locations ${e.message}")
            }
        }
    }


    protected fun refreshCustomers() {
        customerLocationList = customersLocations?.data?.locations?.toMutableList()
        customerLocationList?.forEach { pickUpStation ->
            addStations(pickUpStation, KoboLocationType.CUSTOMER)
        }
    }

    protected fun refreshStations() {
        koboStationLocationsList = koboStationLocations?.data?.locations?.toMutableList()
        koboStationLocationsList?.forEach { koboStation ->
            addStations(koboStation, KoboLocationType.STATION)
        }
    }



    protected fun setupClusterManager(listTrucks: List<Trucks>) {
        clusterManager.renderer = MarkerClusterRenderer(this, mMap, clusterManager)
        addTruckClusters(listTrucks)
        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)
        clusterManager.cluster()

        try {
            val boundsBuilder = LatLngBounds.Builder()
            truckMarkerManager.values.toList().map { model ->
                boundsBuilder.include(
                        model.lastKnownLocation?.coordinates?.get(1)?.let {
                            LatLng(
                                    it,
                                    model.lastKnownLocation.coordinates[0]
                            )
                        }
                )
            }

            val bounds = boundsBuilder.build()
            val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 50)
            if (currentDisplayMode == DisplayMode.ALL) {
                mMap.uiSettings.isRotateGesturesEnabled = false
            }
//            mMap.animateCamera(cu)
//                    setupClusterManager()
//                    moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.center, 10.0F))
        } catch (e: Exception) {
        }
    }

//    protected fun addClusters(listTrucks: List<TruckModelDataParser>) {
//        if (listTrucks.isNotEmpty()) {
//            listTrucks.forEach { e -> clusterManager.addItem(MarkerClusterItem(e)) }
//        }
//    }

    protected fun addTruckClusters(listTrucks: List<Trucks>) {
        if (listTrucks.isNotEmpty()) {
            listTrucks.forEach { e -> e.lastKnownLocation?.let { clusterManager.addItem(TruckClusterItem(e)) } }
        }
    }


    private fun getMarkerFromClusterCollections(title: String): Marker {
        val listOfMarkers = clusterManager.markerCollection.markers.toMutableList()
        return listOfMarkers.filter { eachMarker -> eachMarker.title == title }.toList()[0]
    }

    protected fun getMarkerFromClusterCollections(clusterItem: TruckClusterItem): Marker {
        return getMarkerFromClusterCollections(clusterItem.title)
    }
}