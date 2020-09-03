package com.kobo.mobile_map_core.mobile_map_core.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import kotlinx.android.synthetic.main.general_search_item.view.*
import java.util.*

class GeneralSearchAdapter(
        private val tuckList: ArrayList<Trucks>, val itemClickListener: OnSearchResultItemClickListener
) : RecyclerView.Adapter<GeneralSearchDataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            GeneralSearchDataViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.general_search_item, parent,
                            false
                    )
            )

    override fun getItemCount(): Int = tuckList.size

    override fun onBindViewHolder(holder: GeneralSearchDataViewHolder, position: Int) =
            holder.bind(tuckList[position],itemClickListener)

    fun addData(list: List<Trucks>?) {
        tuckList.clear()
        list?.let { tuckList.addAll(it) }
    }

}


class GeneralSearchDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(truck: Trucks, clickListener: OnSearchResultItemClickListener) {
        itemView.textTruckReg.text = truck.regNumber
        itemView.textTruckAsset.text =  "${truck.assetClass?.type} ${truck.assetClass?.size} ${truck.assetClass?.unit}"

        itemView.setOnClickListener {
            clickListener.onItemClicked(truck)
        }
//            Glide.with(itemView.imageViewAvatar.context)
//                .load(suggestedPlaces.avatar)
//                .into(itemView.imageViewAvatar)
    }
}


interface OnSearchResultItemClickListener{
    fun onItemClicked(truck: Trucks)
}