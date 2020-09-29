package com.kobo.mobile_map_core.mobile_map_core.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kobo.mobile_map_core.mobile_map_core.MobileMapCorePlugin
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiHelper
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiServiceImpl
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.ActiveTripsData
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.ActiveTripsDataResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.ActiveTripsFilteredListAdapter
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.OnActiveTripItemClickListener
import com.kobo.mobile_map_core.mobile_map_core.ui.base.ViewModelFactory
import com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel.ActiveTripsViewModel
import com.todkars.shimmer.ShimmerRecyclerView
import com.xdev.mvvm.utils.Status

private const val ARG_PARAM1 = "param1"


class FilteredActiveTrips : Fragment(), OnActiveTripItemClickListener {


    private lateinit var callback: OnTripInfoClickedListener
    private lateinit var callBack: OnTripInfoCloseButtonClickListener
    private lateinit var switchToMapClickListener: SwitchToMapClickListener
    private var userTypeAndId: String? = null


    fun setOnTripInfoClickedListener(callback: OnTripInfoClickedListener) {
        this.callback = callback
    }

    fun setOnTripInfoCloseButtonClickListener(callback: OnTripInfoCloseButtonClickListener) {
        this.callBack = callback
    }

    fun setSwitchToMapClickListener(swToMapClickListener: SwitchToMapClickListener) {
        this.switchToMapClickListener = swToMapClickListener
    }


    private var filterBy: String? = null
    private lateinit var listShimmerFilteredActiveTrips: ShimmerRecyclerView
    private lateinit var closeFragmentIcon: ImageView
    private lateinit var switchToMap: ImageView
    private lateinit var adapter: ActiveTripsFilteredListAdapter
    private lateinit var getActiveTripsViewModel: ActiveTripsViewModel
    private  lateinit var data: ActiveTripsData;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            filterBy = it.getString(ARG_PARAM1)
        }

        val shaper = requireActivity().let {
            PreferenceManager.getDefaultSharedPreferences(it)
        }

        userTypeAndId = shaper.getString(MobileMapCorePlugin.KEY_USER_TYPE_AND_ID, "")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        userTypeAndId?.let {
            getActiveTripsViewModel.fetchActiveTrips(userTypeAndId = it, filterBy = filterBy)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_filtered_active_trips, container, false)



        listShimmerFilteredActiveTrips = view.findViewById(R.id.listShimmerFilteredActiveTrips)
        closeFragmentIcon = view.findViewById(R.id.closeFragmentIcon)
        closeFragmentIcon.setOnClickListener {
            callBack.onTripInfoCloseButtonClick()
        }

        switchToMap = view.findViewById(R.id.switchToMap)
        switchToMap.setOnClickListener {
            switchToMapClickListener.onSwitchToMapClickListener(data)
        }


        setupUI()
        setupViewModel()
        setupObserver()
        return view
    }


    companion object {

        @JvmStatic
        fun newInstance(filterBy: String?) =
                FilteredActiveTrips().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, filterBy)
                    }
                }
    }

    private fun setupViewModel() {
        getActiveTripsViewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiServiceImpl( requireActivity())))
        ).get(ActiveTripsViewModel::class.java)
    }

    private fun setupUI() {
        listShimmerFilteredActiveTrips.layoutManager = LinearLayoutManager(activity)
        adapter = ActiveTripsFilteredListAdapter(arrayListOf(), this)
        listShimmerFilteredActiveTrips.addItemDecoration(
                DividerItemDecoration(
                        listShimmerFilteredActiveTrips.context,
                        (listShimmerFilteredActiveTrips.layoutManager as LinearLayoutManager).orientation
                )
        )
        listShimmerFilteredActiveTrips.adapter = adapter
        listShimmerFilteredActiveTrips.showShimmer()
    }


    private fun setupObserver() {
        getActiveTripsViewModel.getActiveTripsData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
//                    progressBar.visibility = View.GONE
                    listShimmerFilteredActiveTrips.hideShimmer()
                    it.data?.let { activeTrips -> bootstrapActiveTrips(activeTrips) }
//                    recyclerView.visibility = View.VISIBLE
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

    private fun bootstrapActiveTrips(activeTrips: ActiveTripsDataResponse) {
        data = activeTrips.data
        activeTrips.data.tripsData.trucks?.let { it1 -> renderList(it1) }

    }


    private fun renderList(results: List<Trucks?>) {
        adapter.addData(results)
        adapter.notifyDataSetChanged()
    }

    override fun onItemClicked(tripInfo: Trucks) {
        callback.onSelect(tripInfo)
    }


    interface OnTripInfoClickedListener {
        fun onSelect(selectedTripInfo: Trucks)
    }

    interface OnTripInfoCloseButtonClickListener {
        fun onTripInfoCloseButtonClick()
    }

    interface SwitchToMapClickListener {
        fun onSwitchToMapClickListener(data: ActiveTripsData)
    }
}