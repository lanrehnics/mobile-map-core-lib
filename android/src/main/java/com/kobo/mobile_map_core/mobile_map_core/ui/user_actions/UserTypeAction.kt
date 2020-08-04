package com.kobo.mobile_map_core.mobile_map_core.ui.user_actions

import android.content.Context
import android.text.Editable
import androidx.fragment.app.FragmentManager
import com.google.android.libraries.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager
import com.kobo.mobile_map_core.mobile_map_core.data.models.OrderClusterItem
import com.kobo.mobile_map_core.mobile_map_core.data.models.PrepareFragmentModel
import com.kobo.mobile_map_core.mobile_map_core.data.models.TruckClusterItem
import com.kobo.mobile_map_core.mobile_map_core.data.models.asset_class_model.AssetClasses

interface UserTypeAction {
    fun openSearchAvailableTrucks()
    fun closeSearchBottomSheet()
    fun setUpSpinner(context: Context, list: List<AssetClasses>, onSelect: (result: AssetClasses) -> Unit)
    fun setSearchForAvailableTrucks(isSearch: Boolean)
    fun setEtPickUp(pickupString: Editable?)
    fun setMap(map: GoogleMap, clusterMan: ClusterManager<TruckClusterItem>, ordersClusterMan: ClusterManager<OrderClusterItem>)
    fun setClusterManagerClickListener(onSelect: (result: String?) -> Unit)
    fun  expandSearchBottomSheet()
    fun  switchToListFromMap(data: Any, prepareFragment: (fragment: PrepareFragmentModel) -> Unit)
    fun onSelectFromList(selectedInfo: Any, supportFragmentManager: FragmentManager, setItemDetails: (displayFor: String) -> Unit)
    fun onTripInfoCloseButtonClick(supportFragmentManager: FragmentManager)
    fun onSwitchToMapClickListener(supportFragmentManager: FragmentManager)

}