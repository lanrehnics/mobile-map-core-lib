package com.kobo.mobile_map_core.mobile_map_core.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import kotlinx.android.synthetic.main.item_available_truck.view.*
import java.util.*

class AvailableTruckListAdapter(private val availableTruckList: ArrayList<Trucks?>, val itemClickListener: OnAvailableTruckItemClickListener) : RecyclerView.Adapter<AvailableTruckListAdapterAdapterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableTruckListAdapterAdapterHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_available_truck, parent, false)
        return AvailableTruckListAdapterAdapterHolder(view)
    }

    override fun onBindViewHolder(holder: AvailableTruckListAdapterAdapterHolder, position: Int) = holder.bind(availableTruckList[position], itemClickListener)


    fun addData(list: List<Trucks>) {
        availableTruckList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = availableTruckList.size
}


class AvailableTruckListAdapterAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(truckInfo: Trucks?, clickListener: OnAvailableTruckItemClickListener) {

        itemView.setOnClickListener {
            truckInfo?.let { it1 -> clickListener.onItemClicked(it1) }
        }
        itemView.tvTruckRegNo.text = truckInfo!!.regNumber
        itemView.tvAssetInfo.text = "${truckInfo.assetClass?.type} ${truckInfo.assetClass?.size} ${truckInfo.assetClass?.unit}"
        itemView.tvCurrentNavigation.text = truckInfo.lastKnownLocation?.address
        itemView.tvEta.text = truckInfo.tripDetail?.expectedETAObject?.text
        itemView.rbTruckRate.stepSize = (0.5f)
        itemView.rbTruckRate.rating =0.1F

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

interface OnAvailableTruckItemClickListener {
    fun onItemClicked(truckInfo: Trucks)
}