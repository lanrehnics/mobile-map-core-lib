package com.kobo.mobile_map_core.mobile_map_core.map

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.*
import com.google.maps.android.clustering.ClusterManager
import com.kobo.mobile_map_core.mobile_map_core.MobileMapCorePlugin
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.animation.LatLngInterpolator
import com.kobo.mobile_map_core.mobile_map_core.animation.MarkerAnimation
import com.kobo.mobile_map_core.mobile_map_core.enums.AppType
import com.kobo.mobile_map_core.mobile_map_core.models.ClearCommand
import com.kobo.mobile_map_core.mobile_map_core.models.DisplayMode
import com.kobo.mobile_map_core.mobile_map_core.models.TripStatus
import com.kobo.mobile_map_core.mobile_map_core.models.TruckModelDataParser
import com.kobo360.map.MarkerClusterRenderer
import com.kobo360.models.CustomersLocations
import com.kobo360.models.Locations
import com.kobo.mobile_map_core.mobile_map_core.services.MapService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener
import java.lang.Exception
import java.util.*

abstract class BaseMapActivity : AppCompatActivity() {


    //Private properties
    private var fireStoreSearchEventListener: ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()
    private lateinit var collectionRef: CollectionReference
    private lateinit var geoFireStore: GeoFirestore
    private lateinit var polylineOptions: PolylineOptions
    private lateinit var blackPolylineOptions: PolylineOptions
    private lateinit var blackPolyline: Polyline
    private lateinit var greyPolyLine: Polyline


    //Protected variables ...
    protected lateinit var context: Context
    protected var isMappedFiltered = false
    protected var customersLocations: CustomersLocations? = null
    protected lateinit var fusedLocationClient: FusedLocationProviderClient
    protected lateinit var mapService: MapService
    protected lateinit var mMap: GoogleMap
    protected lateinit var mMapView: View
    protected lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    protected lateinit var fabMyLocation: View
    protected lateinit var fabTripFilter: View
    protected lateinit var fabOpenSearch: View
    protected lateinit var btnApply: Button
    protected lateinit var backArrowButton: ImageButton
    protected lateinit var btnCloseDrawer: View
    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var navigationView: NavigationView
    protected lateinit var toggle: ActionBarDrawerToggle
    protected lateinit var toolbar: Toolbar
    protected lateinit var statusPositioned: CheckBox
    protected lateinit var statusInpremise: CheckBox
    protected lateinit var statusLoaded: CheckBox
    protected lateinit var statusTransporting: CheckBox
    protected lateinit var statusAtDestination: CheckBox
    protected lateinit var statusAvailable: CheckBox
    protected lateinit var activeTrips: CheckBox
    protected lateinit var flaggedTrips: CheckBox
    protected lateinit var btnCloseTruckInfo: ImageButton
    protected lateinit var currentLatLng: LatLng
    protected lateinit var bottomSheetHeader: LinearLayout
    protected lateinit var tvTripId: TextView
    protected lateinit var tvTripStatus: TextView
    protected lateinit var tvCustName: TextView
    protected lateinit var tvPickUp: TextView
    protected lateinit var tvDestination: TextView
    protected lateinit var tvDriverName: TextView
    protected lateinit var tvDriverMobile: TextView
    protected lateinit var tvRegNumber: TextView
    protected lateinit var tvEtt: TextView
    protected var isSearchOn = false
    protected lateinit var clusterManager: ClusterManager<MarkerClusterItem>
    protected var markerManager: MutableMap<String, Marker> = mutableMapOf()


    protected lateinit var tripFilterLinearLayout: LinearLayout

    private var parserCounter = 0

    protected var truckMarkerManager: MutableMap<String, TruckModelDataParser> = mutableMapOf()
    var pickUpStations: MutableMap<Marker, Locations> = mutableMapOf()
    var selectedTruck: TruckModelDataParser? = null

    //    protected val listTruckModel: MutableList<TruckModelDataParser?> = ArrayList()
    protected val listTripStatusFilter: MutableList<String?> = ArrayList()

    protected lateinit var selectedMarker: Marker
    protected var currentDisplayMode: DisplayMode = DisplayMode.ALL

    protected fun showMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }


    private fun focusOnTruck(truckId: String) {
        backArrowButton.visibility = View.INVISIBLE
        btnCloseTruckInfo.visibility = View.VISIBLE

        val docRef = db.collection("Trucks").document(truckId)
        fireStoreSearchEventListener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(MapsActivity.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {

                fireStoreSearchEventListener?.remove()
                Log.d(MapsActivity.TAG, "Current data: ${snapshot.data}")
                val searchResult = snapshot.toObject(TruckModelDataParser::class.java)

                if (searchResult != null) {
                    isSearchOn = true
                    mMap.uiSettings.isRotateGesturesEnabled = true
                    mMap.addMarker(
                            MarkerOptions()
                                    .position(toLatLng(searchResult.l))
                                    .title(searchResult.d.reg_number)
                                    .rotation(searchResult.d.bearing.toFloat())
                                    .icon(truckFromStatus(searchResult))
                    )
                    mMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.Builder().target(
                                            LatLng(searchResult.l.latitude, searchResult.l.longitude)
                                    )
                                            .zoom(16.5f).build()
                            )
                    )
                }


            } else {
                Log.d(MapsActivity.TAG, "Current data: null")
            }
        }

    }

    protected fun toLatLng(geoPoint: GeoPoint): LatLng {
        return LatLng(geoPoint.latitude, geoPoint.longitude)
    }

    protected fun toGeoPoint(latLng: LatLng): GeoPoint {
        return GeoPoint(latLng.latitude, latLng.longitude)
    }

    protected fun clearMapAndData(command: ClearCommand) {

        clusterManager.clearItems()
        when (command) {
            ClearCommand.MAP -> {
                mMap.clear()
//                listTripStatusFilter.clear()
            }
            ClearCommand.DATA -> {
                truckMarkerManager.clear()
                markerManager.clear()
                listTripStatusFilter.clear()
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
                backArrowButton.visibility = View.VISIBLE
                btnCloseTruckInfo.visibility = View.INVISIBLE
                listTripStatusFilter.clear()
            }
        }
    }

    protected fun resetView() {
        try {
            selectedTruck = null
            fireStoreSearchEventListener?.remove()
            currentDisplayMode = DisplayMode.ALL
            btnCloseTruckInfo.visibility = View.INVISIBLE
            clearMapAndData(ClearCommand.ALL)

            bootStrapPickupStationsAndTrucks()
        } catch (e: Exception) {
        }
    }

    protected fun addTrucks(model: TruckModelDataParser?) {
        try {
//            if (truckMarkerManager.size <= 50) {
            if (model != null && model.l.latitude != 0.0) {
                truckMarkerManager[model.d.reg_number] = model
            }
//            }
        } catch (error: Exception) {
            error.message?.let { Log.d("partner", it) }
        }

    }

    protected fun addStations(model: Locations?) {
        try {
            if (model != null && model.lat > 0.0 && model.long > 0.0) {
                val marker = mMap.addMarker(
                        MarkerOptions().position(LatLng(model.lat, model.long))
                                .icon(
                                        BitmapDescriptorFactory.fromResource(
                                                R.drawable.warehouse
                                        )
                                )
                                .title(model.contact_name)
                                .snippet("Pick up location at ${model.address}")
                )
                pickUpStations[marker] = model
            }
        } catch (error: Exception) {
            Log.d("partner", error.message)
        }

    }

    protected fun setTruckDetails() {
        tvTripId.text = selectedTruck?.d?.tripId
        tvTripStatus.text = selectedTruck?.d?.status
        tvCustName.text = selectedTruck?.d?.customerName
        tvPickUp.text = selectedTruck?.d?.pickUpStation?.address
        tvDestination.text = selectedTruck?.d?.deliveryStation?.address
        tvDriverName.text = selectedTruck?.d?.driverName
        tvDriverMobile.text = selectedTruck?.d?.driverMobile
        tvRegNumber.text = selectedTruck?.d?.reg_number
        tvEtt.text = "O Hour(s)"
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

        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLACK)
        polylineOptions.width(10f)
        polylineOptions.startCap(SquareCap())
        polylineOptions.endCap(SquareCap())
        polylineOptions.jointType(JointType.ROUND)
        polylineOptions.addAll(polyLineList)
        greyPolyLine = mMap.addPolyline(polylineOptions)

        blackPolylineOptions = PolylineOptions()
        blackPolylineOptions.width(10f)
        blackPolylineOptions.color(Color.BLACK)
        blackPolylineOptions.startCap(SquareCap())
        blackPolylineOptions.endCap(SquareCap())
        blackPolylineOptions.jointType(JointType.ROUND)
        blackPolyline = mMap.addPolyline(blackPolylineOptions)

        mMap.addMarker(
                MarkerOptions()
                        .position(polyLineList.get(polyLineList.size - 1))
                        .icon(
                                BitmapDescriptorFactory.fromResource(
                                        R.drawable.warehouse
                                )
                        )
                        .title(selectedTruck?.d?.reg_number)
                        .snippet("Delivery up location at ${selectedTruck?.d?.deliveryStation?.address}")
        )

    }

    protected fun bootStrapPickupStationsAndTrucks() {
        clearMapAndData(ClearCommand.ALL)
        GlobalScope.launch(Dispatchers.Main) {
            try {
                runGeoQuery(verifyCurrentLocation(toGeoPoint(currentLatLng)))
                customersLocations = mapService.fetchCustomersLocations()
                customersLocations?.data?.locations?.forEach { pickUpStation ->
                    addStations(pickUpStation)
                }
            } catch (e: Exception) {
                Log.e(MapsActivity.TAG, "Error fetching customers locations ${e.message}")
            }
        }
    }

    private fun runGeoQuery(userCurrentLocation: GeoPoint) {
        val geoQuery = geoFireStore.queryAtLocation(userCurrentLocation, MapsActivity.QUERY_RADIUS)
        geoQuery.addGeoQueryDataEventListener(object : GeoQueryDataEventListener {
            override fun onDocumentEntered(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
                try {
                    if (truckMarkerManager.size < 200) {
                        val model = documentSnapshot.toObject(TruckModelDataParser::class.java)
                        addTrucks(model)
                    }
                } catch (error: Exception) {
                    Log.e(MapsActivity.TAG, "Error parsing data")
                }

//                Log.i(MapsActivity.TAG, listTruckModel.size.toString())
            }

            override fun onDocumentExited(documentSnapshot: DocumentSnapshot) {
//                val model = documentSnapshot.toObject(TruckModelDataParser::class.java)
//                val marker = truckMarkerManager[model?.d?.reg_number]

                //TODO work on removing marker from clustered markers
//                marker?.remove()
            }

            override fun onDocumentMoved(documentSnapshot: DocumentSnapshot, location: GeoPoint) {

                try {
                    val model = documentSnapshot.toObject(TruckModelDataParser::class.java)
                    val truckModelDataParser = truckMarkerManager[model?.d?.reg_number]

                    model?.d?.bearing?.let { it ->
                        MarkerAnimation.animateMarkerToGB(
                                if (selectedTruck != null) {
                                    markerManager[model.d.reg_number]
                                } else {
                                    truckModelDataParser?.d?.reg_number?.let { regNumber ->
                                        getMarkerFromClusterCollections(
                                                regNumber
                                        )
                                    }
                                },
                                model.l.latitude.let { LatLng(it, model.l.longitude) },
                                it,
                                LatLngInterpolator.Spherical()
                        )
                        if (currentDisplayMode == DisplayMode.SINGLE) {

                            mMap.uiSettings.isRotateGesturesEnabled = true
                            mMap.animateCamera(
                                    CameraUpdateFactory.newCameraPosition(
                                            CameraPosition.Builder().target(
                                                    selectedMarker.position
                                            )
                                                    .zoom(16.5f).build()
                                    )
                            )
                        }
                    }
                    clusterManager.cluster()
                } catch (e: Exception) {
                    parserCounter++
                    print(e.message)
                }
            }

            override fun onDocumentChanged(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onGeoQueryReady() {

                Log.e(MapsActivity.TAG, "Unable to parse: $parserCounter Truck info")


                try {
                    val boundsBuilder = LatLngBounds.Builder()
                    truckMarkerManager.values.toList().map { model ->
                        boundsBuilder.include(
                                LatLng(
                                        model.l.latitude,
                                        model.l.longitude
                                )
                        )
                    }


                    val bounds = boundsBuilder.build()

                    val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 50)
                    if (currentDisplayMode == DisplayMode.ALL) {
                        mMap.uiSettings.isRotateGesturesEnabled = false
                    }
                    mMap.animateCamera(cu)
                    setupClusterManager()

//                    moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.center, 10.0F))

                } catch (e: Exception) {
                }

            }

            override fun onGeoQueryError(exception: Exception) {
                Log.e(MapsActivity.TAG, "onGeoQueryError: ", exception)
            }
        })
    }

    protected fun setUpLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MapsActivity.LOCATION_PERMISSION_REQUEST_CODE)
            return
        } else {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                // 3
                if (location != null) {
//                lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                    bootStrapPickupStationsAndTrucks()
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

    protected fun manageTripFilterParam(filterWith: String, bool: Boolean) {
        if (bool) {
            listTripStatusFilter.add(filterWith)
        } else {
            listTripStatusFilter.remove(filterWith)
        }

        btnApply.visibility =
                if (listTripStatusFilter.isNotEmpty()) View.VISIBLE else View.INVISIBLE

    }

    protected fun setupClusterManager() {
        clusterManager.renderer = MarkerClusterRenderer(this, mMap, clusterManager)
        addClusters(truckMarkerManager.values.toList())
        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)
        clusterManager.cluster()
    }

    protected fun addClusters(listTrucks: List<TruckModelDataParser>) {
        if (listTrucks.isNotEmpty()) {
            listTrucks.forEach { e -> clusterManager.addItem(MarkerClusterItem(e)) }
        }
    }


    protected fun configureCollectionReferenceForApp() {


        val appType: String? = PreferenceManager.getDefaultSharedPreferences(context).getString(MobileMapCorePlugin.KEY_APP_TYPE, null)
        val id: String? = PreferenceManager.getDefaultSharedPreferences(context).getString(MobileMapCorePlugin.KEY_ID, null)

        when (appType?.let { AppType.valueOf(it) }) {
            AppType.Squad -> {
                collectionRef = FirebaseFirestore.getInstance().collection("Trucks")
            }
            AppType.Customer -> {
                collectionRef = FirebaseFirestore.getInstance().collection("Customers/$id/TripList")
            }
            AppType.Transporter -> {
                collectionRef = FirebaseFirestore.getInstance().collection("Trucks")
            }

        }

        geoFireStore = GeoFirestore(collectionRef)

    }

    protected fun truckFromStatus(model: TruckModelDataParser): BitmapDescriptor? {
        return BitmapDescriptorFactory.fromResource(
                when {
                    model.d.status == TripStatus.STATUS_DELIVERED -> R.drawable.kobo_truck_blue
                    model.d.flagged -> R.drawable.kobo_truck_red
                    else -> R.drawable.kobo_truck_green
                }
        )
    }

    private fun getMarkerFromClusterCollections(title: String): Marker {
        val listOfMarkers = clusterManager.markerCollection.markers.toMutableList()
        return listOfMarkers.filter { eachMarker -> eachMarker.title == title }.toList()[0]
    }

    protected fun getMarkerFromClusterCollections(clusterItem: MarkerClusterItem): Marker {
        return getMarkerFromClusterCollections(clusterItem.title)
    }

    private fun verifyCurrentLocation(geoPoint: GeoPoint): GeoPoint {
//        return if (geoPoint.latitude > 0 && geoPoint.longitude > 0) {
//            geoPoint
//        } else {
//            MapsActivity.QUERY_CENTER
//        }

        return MapsActivity.QUERY_CENTER

    }
}