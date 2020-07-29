package com.kobo.mobile_map_core.mobile_map_core.ui.map

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.LatLngBounds
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kobo.mobile_map_core.mobile_map_core.MobileMapCorePlugin
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiHelper
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiServiceImpl
import com.kobo.mobile_map_core.mobile_map_core.data.models.ClearCommand
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.DedicatedTruckData
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import com.kobo.mobile_map_core.mobile_map_core.enums.FocusFrom
import com.kobo.mobile_map_core.mobile_map_core.enums.MapDisplayMode
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.DedicatedTruckListAdapter
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.OnDedicatedTruckItemClickListener
import com.kobo.mobile_map_core.mobile_map_core.ui.base.ViewModelFactory
import com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel.DedicatedTruckViewModel
import com.todkars.shimmer.ShimmerRecyclerView
import com.xdev.mvvm.utils.Status
import kotlinx.android.synthetic.main.activity_dedicated_truck.*
import kotlinx.android.synthetic.main.dedicated_truck_bottom_sheet.*

class DedicatedTruckActivity : NewBaseMapActivity(), OnDedicatedTruckItemClickListener {

    private lateinit var listShimmerDedicatedTrucks: ShimmerRecyclerView
    private lateinit var adapter: DedicatedTruckListAdapter
    private lateinit var dedicatedTruckViewModel: DedicatedTruckViewModel
    private lateinit var dedicatedTruckBottomSheet: BottomSheetBehavior<View>
    private lateinit var bottomSheetView: View
    private lateinit var dedicatedTruckData: DedicatedTruckData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dedicated_truck)
        listShimmerDedicatedTrucks = findViewById(R.id.listShimmerDedicatedTrucks)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.elevation = 0f
        actionBar.setDisplayShowHomeEnabled(true)

        initMapStuffs()
        setupUI()
        setupViewModel()
        setupObserver()
        fetchDedicatedTrucks()
    }


    private fun fetchDedicatedTrucks() {
        val shaper = this.let {
            PreferenceManager.getDefaultSharedPreferences(it)
        }
        val userTypeAndId = shaper.getString(MobileMapCorePlugin.KEY_USER_TYPE_AND_ID, "")
        userTypeAndId?.let { dedicatedTruckViewModel.fetchDedicatedTruck(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViewModel() {
        dedicatedTruckViewModel = ViewModelProviders.of(
                this,
                ViewModelFactory(ApiHelper(ApiServiceImpl()))
        ).get(DedicatedTruckViewModel::class.java)
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
                            truckInfo = truckMarkerManager?.get(it.title)!!

                            truckInfo.lastKnownLocation?.let {
                                val marker = mMap.addMarker(
                                        toLatLng(it.coordinates)?.let { it1 ->
                                            MarkerOptions()
                                                    .position(it1)
                                //                                                .title(selectedTruck!!.d.reg_number)
                                                    .rotation(truckInfo.bearing.toFloat())
                                                    .icon(truckFromStatus(truckInfo))
                                        }
                                )
                                bootstrapDedicationBottomSheet()
                                dedicatedTruckBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                            }

                        }


                        override fun onCancel() {}
                    })
            true
        }
    }

    override fun multipleTruckClusteringMode() {
        clearMapAndData(ClearCommand.ALL)
        truckMarkerManager = dedicatedTruckData.truckData.trucks.map { it.regNumber to it }.toMap().toMutableMap()
        setupClusterManager(dedicatedTruckData.truckData.trucks)
    }


    fun openMapFragmentWithAllTrucks(view: View) {
        if (pBar.visibility == View.GONE) {
            switchToList.visibility = View.VISIBLE
            switchToMap.visibility = View.GONE
            displayMode = MapDisplayMode.MultipleTruckClusteringMode
            openMapFragment()
        }

    }


    override fun onBackPressed() {
        if (dedicatedTruckBottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED ||
                dedicatedTruckBottomSheet.state == BottomSheetBehavior.STATE_HIDDEN ||
                bottomSheetView.visibility == View.GONE || bottomSheetView.visibility == View.INVISIBLE) {


//
//            when (focusFrom) {
//                FocusFrom.Map -> {
//                    focusFrom = FocusFrom.Default
//                    dedicatedTruckBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
//                    multipleTruckClusteringMode()
//                }
//                else -> {
            bottomSheetView.visibility = View.GONE
            val count = supportFragmentManager.backStackEntryCount
            if (count == 0) {
                super.onBackPressed()
            } else {
                supportFragmentManager.popBackStack()
                switchToList.visibility = View.GONE
                switchToMap.visibility = View.VISIBLE
            }
//                }
//            }


        } else {
            dedicatedTruckBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            focusFrom = FocusFrom.Default
            if (focusFrom == FocusFrom.Map) {
                multipleTruckClusteringMode()
            }
        }

//        val fragment: Fragment? = supportFragmentManager.findFragmentByTag("filteredActiveTrips")
//        if (fragment != null)
//            supportFragmentManager.beginTransaction().remove(fragment).commit()
//        else
//            super.onBackPressed();

    }

    private fun setupUI() {
        bottomSheetView = findViewById(R.id.dedicatedTruckBottomSheet)
        bottomSheetView.visibility = View.GONE
        dedicatedTruckBottomSheet = BottomSheetBehavior.from(bottomSheetView)
        dedicatedTruckBottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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
        listShimmerDedicatedTrucks.layoutManager = LinearLayoutManager(this)
        adapter = DedicatedTruckListAdapter(arrayListOf(), this)
        listShimmerDedicatedTrucks.addItemDecoration(
                DividerItemDecoration(
                        listShimmerDedicatedTrucks.context,
                        (listShimmerDedicatedTrucks.layoutManager as LinearLayoutManager).orientation
                )
        )
        listShimmerDedicatedTrucks.adapter = adapter
        listShimmerDedicatedTrucks.showShimmer()
    }

    private fun setupObserver() {
        dedicatedTruckViewModel.getDedicatedTruckData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    pBar.visibility = View.GONE
                    listShimmerDedicatedTrucks.hideShimmer()
                    it.data?.let { autoComplete -> renderList(autoComplete.data) }
                }
                Status.LOADING -> {
                    pBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    listShimmerDedicatedTrucks.hideShimmer()
                    pBar.visibility = View.GONE
                }
            }
        })
    }

    override fun onItemClicked(selectedTripInfo: Trucks) {
        truckInfo = selectedTripInfo
        displayMode = MapDisplayMode.SingleTruckFocusMode
        openMapFragment()
        dedicatedTruckBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED

        bootstrapDedicationBottomSheet()

    }

    private fun bootstrapDedicationBottomSheet() {
        bottomSheetView.visibility = View.VISIBLE
        tvTruckRegNo.text = truckInfo.regNumber
        tvAssetInfo.text = "${truckInfo.assetClass?.type} ${truckInfo.assetClass?.size}${truckInfo.assetClass?.unit}"
//        tvRating.text = "N/A"
//        rbTruckRate.rating = 0f
        tvCurrentNavigation.text = truckInfo.lastKnownLocation?.address
        tvDriverName.text = truckInfo.driver.firstName
//        tvDriverRating.text = selectedTripInfo.driver.rating.toString()
//        tvMemberSince.text = "Member since: N/A"

        this.let {
            Glide.with(it)
                    .load(truckInfo.driver.image)
                    .placeholder(ColorDrawable(Color.BLACK))
                    .into(profile_image)
        }

    }

    private fun renderList(results: DedicatedTruckData) {
        dedicatedTruckData = results
        adapter.addData(results.truckData.trucks)
        adapter.notifyDataSetChanged()
    }


    fun switchToListFromMap(view: View) {
        bottomSheetView.visibility = View.GONE
        val count = supportFragmentManager.backStackEntryCount
        if (count > 0) {
            supportFragmentManager.popBackStack()
        }
        switchToList.visibility = View.GONE
        switchToMap.visibility = View.VISIBLE
    }

}