package com.kobo.mobile_map_core.mobile_map_core.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.models.orders.AvailableOrdersData
import com.kobo.mobile_map_core.mobile_map_core.data.models.orders.Orders
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.AvailableOrderListAdapter
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.OnAvailableOrderItemClickListener
import com.kobo.mobile_map_core.mobile_map_core.ui.fragments.`interface`.UseFulFragmentsInterface
import com.todkars.shimmer.ShimmerRecyclerView

private const val ARG_PARAM1 = "param1"


class FragmentSearchAvailableOrders : Fragment(), OnAvailableOrderItemClickListener {


    private lateinit var callback: UseFulFragmentsInterface.OnInfoClickedListener
    private lateinit var callBack: UseFulFragmentsInterface.OnCloseButtonClickListener
    private lateinit var switchToMapClickListener: UseFulFragmentsInterface.SwitchToMapClickListener


    fun setOnTruckInfoClickedListener(callback: UseFulFragmentsInterface.OnInfoClickedListener) {
        this.callback = callback
    }

    fun setOnAvailableTruckCloseButtonClickListener(callback: UseFulFragmentsInterface.OnCloseButtonClickListener) {
        this.callBack = callback
    }

    fun setSwitchToMapClickListener(swToMapClickListener: UseFulFragmentsInterface.SwitchToMapClickListener) {
        this.switchToMapClickListener = swToMapClickListener
    }


    private var truckData: AvailableOrdersData? = null
    private lateinit var listShimmerFilteredActiveTrips: ShimmerRecyclerView
    private lateinit var closeFragmentIcon: ImageView
    private lateinit var switchToMap: ImageView
    private lateinit var adapter: AvailableOrderListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            truckData = it.getSerializable(ARG_PARAM1) as AvailableOrdersData
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_available_orders, container, false)



        listShimmerFilteredActiveTrips = view.findViewById(R.id.listShimmerFilteredActiveTrips)
        closeFragmentIcon = view.findViewById(R.id.closeFragmentIcon)
        closeFragmentIcon.setOnClickListener {
            callBack.onTripInfoCloseButtonClick()
        }

        switchToMap = view.findViewById(R.id.switchToMap)
        switchToMap.setOnClickListener {
            switchToMapClickListener.onSwitchToMapClickListener()
        }


        setupUI()
        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(ordersData: AvailableOrdersData) =
                FragmentSearchAvailableOrders().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_PARAM1, ordersData)
                    }
                }
    }

    private fun setupUI() {
        listShimmerFilteredActiveTrips.layoutManager = LinearLayoutManager(activity)
        adapter = truckData?.orders?.let { ArrayList(it) }?.let {
            AvailableOrderListAdapter(it
                    , this)
        }!!
        listShimmerFilteredActiveTrips.addItemDecoration(
                DividerItemDecoration(
                        listShimmerFilteredActiveTrips.context,
                        (listShimmerFilteredActiveTrips.layoutManager as LinearLayoutManager).orientation
                )
        )
        listShimmerFilteredActiveTrips.adapter = adapter
//        listShimmerFilteredActiveTrips.showShimmer()
    }


    private fun renderList(results: List<Orders>) {
        adapter.addData(results)
        adapter.notifyDataSetChanged()
    }

    override fun onItemClicked(orders: Orders) {
        callback.onSelect(orders)
    }
}