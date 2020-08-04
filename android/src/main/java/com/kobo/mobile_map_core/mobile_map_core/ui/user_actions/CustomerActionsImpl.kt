package com.kobo.mobile_map_core.mobile_map_core.ui.user_actions

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.LatLngBounds
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.ClusterManager
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.models.OrderClusterItem
import com.kobo.mobile_map_core.mobile_map_core.data.models.PrepareFragmentModel
import com.kobo.mobile_map_core.mobile_map_core.data.models.TruckClusterItem
import com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model.AssetClasses
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.TruckData
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import com.kobo.mobile_map_core.mobile_map_core.ui.fragments.FragmentSearchAvailableTrucks
import com.kobo.mobile_map_core.mobile_map_core.ui.map.NewBaseMapActivity

class CustomerActionsImpl(private val overviewBottomSheet: BottomSheetBehavior<View>,
                          private val searchBottomSheet: BottomSheetBehavior<View>,
                          private val itemDetailsBottomSheet: BottomSheetBehavior<View>,
                          private val spinner: Spinner,
                          private var etPickUp: EditText
) : UserTypeAction {


    private var searchForAvailableTrucks: Boolean = false
    private var googleMap: GoogleMap? = null
    private var clusterManager: ClusterManager<TruckClusterItem>? = null
    private var ordersClusterManager: ClusterManager<OrderClusterItem>? = null


    init {

        overviewBottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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


        searchBottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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


    override fun openSearchAvailableTrucks() {
        overviewBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        searchBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun closeSearchBottomSheet() {
        overviewBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        searchBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun setUpSpinner(context: Context, list: List<AssetClasses>, onSelect: (result: AssetClasses) -> Unit) {

        val spinnerAdapter: ArrayAdapter<AssetClasses> = ArrayAdapter(context, R.layout.item_spinner, list)

        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                onSelect.invoke(parent.selectedItem as AssetClasses)
//                setUpSpinner2(assetClasses.size)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    override fun setSearchForAvailableTrucks(isSearch: Boolean) {
        searchForAvailableTrucks = isSearch
    }

    override fun setEtPickUp(pickupString: Editable?) {
        pickupString?.let { etPickUp.text = it }
    }

    override fun setMap(map: GoogleMap, clusterMan: ClusterManager<TruckClusterItem>, ordersClusterMan: ClusterManager<OrderClusterItem>) {
        clusterManager = clusterMan
        ordersClusterManager = ordersClusterMan
        googleMap = map
    }

    override fun setClusterManagerClickListener(onSelect: (result: String?) -> Unit) {
        clusterManager?.setOnClusterClickListener {

            if (it != null) {
                itemDetailsBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                val items = it.items
                val builder = LatLngBounds.Builder()
                for (item in items) {
                    builder.include(item.position)
                }
                val bounds = builder.build()
                val mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
                googleMap?.animateCamera(mCameraUpdate)
                onSelect.invoke(null)
            }
            true
        }
        clusterManager?.setOnClusterItemClickListener {
            googleMap?.animateCamera(
                    CameraUpdateFactory.newLatLng(it.position),
                    object : GoogleMap.CancelableCallback {
                        override fun onFinish() {
                            onSelect.invoke("${it.title},truck")
                        }

                        override fun onCancel() {}
                    })
            true
        }
    }

    override fun expandSearchBottomSheet() {
        searchBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun switchToListFromMap(data: Any, prepareFragment: (fragment: PrepareFragmentModel) -> Unit) {

        val fragmentSearchAvailableTrucks = FragmentSearchAvailableTrucks.newInstance(data as TruckData)
        prepareFragment.invoke(
                PrepareFragmentModel("truck",
                        fragmentSearchAvailableTrucks))

    }

    override fun onSelectFromList(selectedInfo: Any, supportFragmentManager: FragmentManager, setItemDetails: (displayFor: String) -> Unit) {
        val selectedTruck: Trucks? = selectedInfo as Trucks?
        selectedTruck?.lastKnownLocation?.let {
            val marker = googleMap?.addMarker(
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

            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(NewBaseMapActivity.toLatLng(it.coordinates), 14.5f))
        }

        itemDetailsBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED

        val fragment: Fragment? = supportFragmentManager.findFragmentByTag("fragmentSearchAvailableTrucks")
        if (fragment != null)
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        itemDetailsBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED

        setItemDetails.invoke("truck")
    }

    override fun onTripInfoCloseButtonClick(supportFragmentManager: FragmentManager) {
        val fragment: Fragment? = supportFragmentManager.findFragmentByTag("fragmentSearchAvailableTrucks")
        if (fragment != null)
            supportFragmentManager.beginTransaction().remove(fragment).commit()
    }

    override fun onSwitchToMapClickListener(supportFragmentManager: FragmentManager) {
        val fragment: Fragment? = supportFragmentManager.findFragmentByTag("fragmentSearchAvailableTrucks")
        if (fragment != null)
            supportFragmentManager.beginTransaction().remove(fragment).commit()
    }

}