package com.kobo.mobile_map_core.mobile_map_core.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Trips
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import kotlinx.android.synthetic.main.item_filtered_avtive_trip.view.*
import java.util.*

class ActiveTripsFilteredListAdapter(private val values: ArrayList<Trucks?>, val itemClickListener: OnActiveTripItemClickListener) : RecyclerView.Adapter<ActiveTripsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveTripsViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_filtered_avtive_trip, parent, false)
        return ActiveTripsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActiveTripsViewHolder, position: Int) = holder.bind(values[position], itemClickListener)


    fun addData(list: List<Trucks?>) {
        values.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = values.size
}


class ActiveTripsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(tripInfo: Trucks?, clickListener: OnActiveTripItemClickListener) {
        itemView.tvTripId.text = tripInfo!!.tripDetail?.tripReadId
        itemView.tvAssetType.text = "${tripInfo.assetClass?.type} ${tripInfo.assetClass?.size} ${tripInfo.assetClass?.unit}"
        itemView.tvTruckId.text = tripInfo.regNumber
        itemView.tvPickUpAddress.text = tripInfo.tripDetail?.pickupLocation?.address
        itemView.tvDeliveryAddress.text = tripInfo.tripDetail?.deliveryLocation?.address
        itemView.tvEta.text = tripInfo.tripDetail?.expectedETAObject?.text
        itemView.tvCurrentNavigation.text = tripInfo.lastKnownLocation?.address


        itemView.setOnClickListener {
            clickListener.onItemClicked(tripInfo)
        }
//            Glide.with(itemView.imageViewAvatar.context)
//                .load(suggestedPlaces.avatar)
//                .into(itemView.imageViewAvatar)
    }

//    val tvTripId: TextView = view.findViewById(R.id.tvTripId)
//    val tvAssetType: TextView = view.findViewById(R.id.tvAssetType)
//    val tvTruckId: TextView = view.findViewById(R.id.tvTruckId)
//    val tvPickUpAddress: TextView = view.findViewById(R.id.tvPickUpAddress)
//    val tvDeliveryAddress: TextView = view.findViewById(R.id.tvDeliveryAddress)
//    val tvEta: TextView = view.findViewById(R.id.tvEta)
//    val tvCurrentNavigation: TextView = view.findViewById(R.id.tvCurrentNavigation)

}

interface OnActiveTripItemClickListener {
    fun onItemClicked(tripInfo: Trucks)
}