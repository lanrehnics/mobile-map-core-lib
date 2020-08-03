package com.kobo.mobile_map_core.mobile_map_core.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kobo.mobile_map_core.mobile_map_core.MobileMapCorePlugin
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiHelper
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiServiceImpl
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.ActiveTripsFragmentListAdapter
import com.kobo.mobile_map_core.mobile_map_core.ui.base.ViewModelFactory
import com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel.ActiveTripsViewModel
import com.todkars.shimmer.ShimmerRecyclerView
import com.xdev.mvvm.utils.Status


private const val ARG_PARAM1 = "param1"

class ActiveTripsFragmentList : Fragment() {

    private lateinit var getActiveTripsViewModel: ActiveTripsViewModel
    private lateinit var adapter: ActiveTripsFragmentListAdapter
    private lateinit var listShimmerActiveTrips: ShimmerRecyclerView
    private var userTypeAndId: String? = null

    private var mode: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mode = it.getInt(ARG_PARAM1)
        }

        val shaper = requireActivity().let {
            PreferenceManager.getDefaultSharedPreferences(it)
        }
        userTypeAndId = shaper.getString(MobileMapCorePlugin.KEY_USER_TYPE_AND_ID, "")


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userTypeAndId?.let {
            getActiveTripsViewModel.fetchActiveTrips(userTypeAndId = it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_active_trips_list_list, container, false)

        listShimmerActiveTrips = view.findViewById(R.id.listShimmerActiveTrips)


        setupUI()
        setupViewModel()
        setupObserver()
        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(mode: Int) =
                ActiveTripsFragmentList().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, mode)
                    }
                }
    }

    private fun setupViewModel() {
        getActiveTripsViewModel = ViewModelProviders.of(
                this,
                ViewModelFactory(ApiHelper(ApiServiceImpl(requireActivity())))
        ).get(ActiveTripsViewModel::class.java)
    }


    private fun setupUI() {
        listShimmerActiveTrips.layoutManager = LinearLayoutManager(activity)
        adapter = ActiveTripsFragmentListAdapter(arrayListOf(), mode)
        listShimmerActiveTrips.addItemDecoration(
                DividerItemDecoration(
                        listShimmerActiveTrips.context,
                        (listShimmerActiveTrips.layoutManager as LinearLayoutManager).orientation
                )
        )
        listShimmerActiveTrips.adapter = adapter
        listShimmerActiveTrips.showShimmer()
    }


    private fun setupObserver() {
        getActiveTripsViewModel.getActiveTripsData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
//                    progressBar.visibility = View.GONE
                    listShimmerActiveTrips.hideShimmer()

                    if (mode == 0) {
                        it.data?.let { autoComplete -> renderList(autoComplete.data.tripsData.breakdown) }
                    } else {
                        it.data?.let { autoComplete -> renderList(autoComplete.data.tripsData.congestions) }

                    }
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


    private fun renderList(results: List<Any?>?) {
        results?.let {
            adapter.addData(results)
            adapter.notifyDataSetChanged()
        }

    }


}