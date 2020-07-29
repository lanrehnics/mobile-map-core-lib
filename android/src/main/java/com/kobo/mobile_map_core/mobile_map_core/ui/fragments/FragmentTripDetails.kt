package com.kobo.mobile_map_core.mobile_map_core.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import java.io.Serializable
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Trips
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.ActiveTripsFragmentListAdapter
import com.kobo.mobile_map_core.mobile_map_core.ui.map.NavigationActivity
import com.kobo.mobile_map_core.mobile_map_core.ui.map.NewBaseMapActivity
import de.hdodenhof.circleimageview.CircleImageView

private const val ARG_PARAM = "param"

class FragmentTripDetails : Fragment() {
    private lateinit var callBack: FragmentTripDetails.OnStartNavigationClickListener
    private var tripInfo: Trucks? = null

    private lateinit var tvTripId: TextView
    private lateinit var tvPickUpAddress: TextView
    private lateinit var tvDropOffAddressOne: TextView
    private lateinit var tvDropOffAddressTwo: TextView
    private lateinit var tvDeliveryAddress: TextView
    private lateinit var tvEta: TextView
    private lateinit var tvSalesNo: TextView
    private lateinit var tvWaybillNo: TextView
    private lateinit var tvTruckRegNo: TextView
    private lateinit var tvAssetInfo: TextView
    private lateinit var tvRating: TextView
    private lateinit var rbTruckRate: RatingBar
    private lateinit var ratingBarDriverRating: RatingBar
    private lateinit var tvMemberSince: TextView
    private lateinit var tvCargoDescription: TextView
    private lateinit var tvCargoSize: TextView
    private lateinit var tvCurrentNavigation: TextView
    private lateinit var tvDriverName: TextView
    private lateinit var tvDriverRating: TextView
    private lateinit var profile_image: CircleImageView


    fun setOnStartNavigationClickListener(callback: FragmentTripDetails.OnStartNavigationClickListener) {
        this.callBack = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tripInfo = it.getSerializable(ARG_PARAM) as Trucks
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_trip_details, container, false)
        val tvStartNavigationForTrip = view.findViewById<TextView>(R.id.tvStartNavigationForTrip)
        tvStartNavigationForTrip.setOnClickListener(View.OnClickListener {

            callBack?.oyaStart(tripInfo)
        })
//        view.findViewById<>()

        setUpView(view)
        setValues(tripInfo)

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(tripInfo: Trucks) =
                FragmentTripDetails().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_PARAM, tripInfo)
                    }
                }
    }


    private fun setUpView(view: View) {
        tvTripId = view.findViewById(R.id.tvTripId)
        tvPickUpAddress = view.findViewById(R.id.tvPickUpAddress)
        tvDropOffAddressOne = view.findViewById(R.id.tvDropOffAddressOne)
        tvDropOffAddressTwo = view.findViewById(R.id.tvDropOffAddressTwo)
        tvDeliveryAddress = view.findViewById(R.id.tvDeliveryAddress)
        tvEta = view.findViewById(R.id.tvEta)
        tvSalesNo = view.findViewById(R.id.tvSalesNo)
        tvWaybillNo = view.findViewById(R.id.tvWaybillNo)
        tvTruckRegNo = view.findViewById(R.id.tvTruckRegNo)
        tvAssetInfo = view.findViewById(R.id.tvAssetInfo)
        tvRating = view.findViewById(R.id.tvRating)
        rbTruckRate = view.findViewById(R.id.rbTruckRate)
        tvMemberSince = view.findViewById(R.id.tvMemberSince)
        tvCargoDescription = view.findViewById(R.id.tvCargoDescription)
        tvCargoSize = view.findViewById(R.id.tvCargoSize)
        tvCurrentNavigation = view.findViewById(R.id.tvCurrentNavigation)
        tvDriverName = view.findViewById(R.id.tvDriverName)
        tvDriverRating = view.findViewById(R.id.tvDriverRating)
        ratingBarDriverRating = view.findViewById(R.id.ratingBarDriverRating)
        profile_image = view.findViewById(R.id.profile_image)
    }

    private fun setValues(trips: Trucks?) {
        //TODO Review all the N/A
        tvTripId.text = trips!!.tripDetail?.tripReadId
        tvPickUpAddress.text = trips.tripDetail?.pickupLocation?.address
        tvDropOffAddressOne.text = "____"
        tvDropOffAddressTwo.text = "____"
        tvDeliveryAddress.text = trips.tripDetail?.deliveryLocation?.address
        tvEta.text = trips.tripDetail?.expectedETAObject?.text
        tvSalesNo.text =  trips.tripDetail?.salesOrderNo
        tvWaybillNo.text = trips.tripDetail?.waybillNo
        tvTruckRegNo.text = trips.regNumber
        tvAssetInfo.text = "${trips.assetClass?.type} ${trips.assetClass?.size}${trips.assetClass?.unit}"
        tvRating.text = "N/A"
        rbTruckRate.rating = 0F
        tvMemberSince.text = "N/A"
        tvCargoDescription.text = trips.tripDetail?.goodType
        tvCargoSize.text = "N/A"
        tvCurrentNavigation.text = trips.lastKnownLocation?.address
        tvDriverName.text = trips.driver.firstName
        tvDriverRating.text = trips.driver.rating.toString()
        ratingBarDriverRating.rating = trips.driver.rating.toFloat()
        activity?.let {
            Glide.with(it)
                    .load(trips.driver.image)
                    .placeholder(ColorDrawable(Color.BLACK))
                    .into(profile_image)
        };

    }

    interface OnStartNavigationClickListener {
        fun oyaStart(selectedTripInfo: Trucks?)
    }

}