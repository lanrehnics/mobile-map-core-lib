package com.kobo.mobile_map_core.mobile_map_core.ui.adapter

import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.Autocomplete
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kobo.mobile_map_core.mobile_map_core.R
import kotlinx.android.synthetic.main.item_auto_complete.view.*
import java.util.*

class AutoCompleteAdapter(
        private val suggestedPlaces: ArrayList<Autocomplete>, val itemClickListener: OnAutoCompleteItemClickListener
) : RecyclerView.Adapter<DataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            DataViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.item_auto_complete, parent,
                            false
                    )
            )

    override fun getItemCount(): Int = suggestedPlaces.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
            holder.bind(suggestedPlaces[position],itemClickListener)

    fun addData(list: List<Autocomplete>?) {
        suggestedPlaces.clear()
        list?.let { suggestedPlaces.addAll(it) }
    }

}


class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(suggestedPlaces: Autocomplete, clickListener: OnAutoCompleteItemClickListener) {
        itemView.textViewUserName.text = suggestedPlaces.description
        itemView.textViewUserEmail.text = suggestedPlaces.description

        itemView.setOnClickListener {
            clickListener.onItemClicked(suggestedPlaces)
        }
//            Glide.with(itemView.imageViewAvatar.context)
//                .load(suggestedPlaces.avatar)
//                .into(itemView.imageViewAvatar)
    }
}


interface OnAutoCompleteItemClickListener{
    fun onItemClicked(address: Autocomplete)
}