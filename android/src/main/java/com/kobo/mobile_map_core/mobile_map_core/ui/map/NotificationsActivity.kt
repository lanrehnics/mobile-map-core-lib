package com.kobo.mobile_map_core.mobile_map_core.ui.map

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiHelper
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiServiceImpl
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import com.kobo.mobile_map_core.mobile_map_core.data.models.notifications.GeoNotification
import com.kobo.mobile_map_core.mobile_map_core.data.models.notifications.NotificationsData
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.DedicatedTruckListAdapter
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.NotificationsAdapter
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.OnDedicatedTruckItemClickListener
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.OnNotificationItemClickListener
import com.kobo.mobile_map_core.mobile_map_core.ui.base.ViewModelFactory
import com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel.NotificationsViewModel
import com.todkars.shimmer.ShimmerRecyclerView
import com.xdev.mvvm.utils.Status
import java.util.*

class NotificationsActivity : AppCompatActivity(), OnDedicatedTruckItemClickListener, OnNotificationItemClickListener {

    private lateinit var listShimmerNotifications: ShimmerRecyclerView
    private lateinit var adapter: NotificationsAdapter
    private lateinit var notificationViewModel: NotificationsViewModel
    private lateinit var notificationBottom: BottomSheetBehavior<View>
    private lateinit var bottomSheetView: View
    private var dedicatedTruckData: NotificationsData? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        listShimmerNotifications = findViewById(R.id.listShimmerNotifications)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.elevation = 0f
        actionBar.setDisplayShowHomeEnabled(true)

        listShimmerNotifications.layoutManager = LinearLayoutManager(this)
        adapter = NotificationsAdapter(arrayListOf(), this)
        listShimmerNotifications.addItemDecoration(
                DividerItemDecoration(
                        listShimmerNotifications.context,
                        (listShimmerNotifications.layoutManager as LinearLayoutManager).orientation
                )
        )
        listShimmerNotifications.adapter = adapter
        listShimmerNotifications.showShimmer()

        setupViewModel()
        setupObserver()
        fetchNotifications()
    }


    private fun fetchNotifications() {
         notificationViewModel.fetchNotifications()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViewModel() {
        notificationViewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiServiceImpl(this)))
        ).get(NotificationsViewModel::class.java)
    }




    private fun setupObserver() {
        notificationViewModel.getNotifications().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    listShimmerNotifications.hideShimmer()
                    it.data?.let { autoComplete -> renderList(autoComplete.data) }
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    listShimmerNotifications.hideShimmer()
                }
            }
        })
    }

    override fun onItemClicked(selectedTripInfo: Trucks) {

    }

    private fun renderList(results: NotificationsData) {
        dedicatedTruckData = results
        val notis: List<GeoNotification> = results.notification ?: ArrayList()
        adapter.addData(notis)
        adapter.notifyDataSetChanged()
    }


    fun switchToListFromMap(view: View) {
        bottomSheetView.visibility = View.GONE
        val count = supportFragmentManager.backStackEntryCount
        if (count > 0) {
            supportFragmentManager.popBackStack()
        }
//        switchToMap.visibility = View.VISIBLE
    }

    override fun onItemClicked(notification: GeoNotification?) {
    }

}