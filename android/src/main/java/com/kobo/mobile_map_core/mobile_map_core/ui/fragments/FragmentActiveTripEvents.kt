package com.kobo.mobile_map_core.mobile_map_core.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.ActiveTripEventsListAdapter
import com.todkars.shimmer.ShimmerRecyclerView

private const val ARG_PARAM = "param"

class FragmentActiveTripEvents : Fragment() {

    private var tripInfo: Trucks? = null
    private lateinit var adapter: ActiveTripEventsListAdapter
    private lateinit var listShimmerActiveTripEvents: ShimmerRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tripInfo = it.getSerializable(ARG_PARAM) as Trucks
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_active_trip_events, container, false)

        listShimmerActiveTripEvents = view.findViewById(R.id.listShimmerActiveTripEvents)
        listShimmerActiveTripEvents.layoutManager = LinearLayoutManager(activity)
        adapter = ActiveTripEventsListAdapter(arrayListOf())

        tripInfo?.events?.let {  adapter.addData(it)}

        listShimmerActiveTripEvents.addItemDecoration(
                DividerItemDecoration(
                        listShimmerActiveTripEvents.context,
                        (listShimmerActiveTripEvents.layoutManager as LinearLayoutManager).orientation
                )
        )
        listShimmerActiveTripEvents.adapter = adapter
//        listShimmerActiveTripEvents.showShimmer()

        return view;
    }

    companion object {
        @JvmStatic
        fun newInstance(tripInfo: Trucks) =
                FragmentActiveTripEvents().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_PARAM, tripInfo)
                    }
                }
    }
}