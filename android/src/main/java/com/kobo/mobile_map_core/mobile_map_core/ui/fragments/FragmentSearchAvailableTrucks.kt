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
import com.kobo.mobile_map_core.mobile_map_core.data.models.available_trucks.TruckData
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.AvailableTruckListAdapter
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.OnAvailableTruckItemClickListener
import com.todkars.shimmer.ShimmerRecyclerView

private const val ARG_PARAM1 = "param1"


class FragmentSearchAvailableTrucks : Fragment(), OnAvailableTruckItemClickListener {


    private lateinit var callback: OnTruckInfoClickedListener
    private lateinit var callBack: OnAvailableTruckCloseButtonClickListener
    private lateinit var switchToMapClickListener: SwitchToMapClickListener


    fun setOnTruckInfoClickedListener(callback: OnTruckInfoClickedListener) {
        this.callback = callback
    }

    fun setOnAvailableTruckCloseButtonClickListener(callback: OnAvailableTruckCloseButtonClickListener) {
        this.callBack = callback
    }

    fun setSwitchToMapClickListener(swToMapClickListener: SwitchToMapClickListener) {
        this.switchToMapClickListener = swToMapClickListener
    }


    private var truckData: TruckData? = null
    private lateinit var listShimmerFilteredActiveTrips: ShimmerRecyclerView
    private lateinit var closeFragmentIcon: ImageView
    private lateinit var switchToMap: ImageView
    private lateinit var adapter: AvailableTruckListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            truckData = it.getSerializable(ARG_PARAM1) as TruckData
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_available_trucks, container, false)



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
        fun newInstance(truckData: TruckData) =
                FragmentSearchAvailableTrucks().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_PARAM1, truckData)
                    }
                }
    }

    private fun setupUI() {
        listShimmerFilteredActiveTrips.layoutManager = LinearLayoutManager(activity)
        adapter = truckData?.trucks?.let { ArrayList(it) }?.let {
            AvailableTruckListAdapter(it
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


    private fun renderList(results: List<Trucks>) {
        adapter.addData(results)
        adapter.notifyDataSetChanged()
    }

    override fun onItemClicked(truckInfo: Trucks) {
        callback.onSelect(truckInfo)
    }


    interface OnTruckInfoClickedListener {
        fun onSelect(selectedTruckInfo: Trucks)
    }

    interface OnAvailableTruckCloseButtonClickListener {
        fun onTripInfoCloseButtonClick()
    }

    interface SwitchToMapClickListener {
        fun onSwitchToMapClickListener()
    }
}