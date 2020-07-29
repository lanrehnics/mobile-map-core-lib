package com.kobo.mobile_map_core.mobile_map_core.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Trips
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import kotlinx.android.synthetic.main.item_dedicated_trucks.view.*

class DedicatedTruckListAdapter(private val values: ArrayList<Trucks?>, val itemClickListener: OnDedicatedTruckItemClickListener) : RecyclerView.Adapter<DedicatedTruckListAdapterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DedicatedTruckListAdapterHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dedicated_trucks, parent, false)
        return DedicatedTruckListAdapterHolder(view)
    }

    override fun onBindViewHolder(holder: DedicatedTruckListAdapterHolder, position: Int) = holder.bind(values[position], itemClickListener)


    fun addData(list: List<Trucks>) {
        values.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = values.size
}


class DedicatedTruckListAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(truckInfo: Trucks?, clickListener: OnDedicatedTruckItemClickListener) {
        itemView.tvTruckId.text = truckInfo!!.tripDetail?.tripReadId
        itemView.tvAssetType.text = "${truckInfo.assetClass?.type} ${truckInfo.assetClass?.size} ${truckInfo.assetClass?.unit}"
        itemView.tvLastKnownLocation.text = truckInfo.lastKnownLocation?.address
        itemView.tvLastUpdated.text = "N/A"
//        itemView.imageTrackingIndicator. = tripInfo.regNumber


        itemView.setOnClickListener {
            clickListener.onItemClicked(truckInfo)
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

interface OnDedicatedTruckItemClickListener {
    fun onItemClicked(truckInfo: Trucks)
}