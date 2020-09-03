package com.kobo.mobile_map_core.mobile_map_core.ui.adapter

import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Breakdown
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Congestions
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kobo.mobile_map_core.mobile_map_core.R
import java.util.*

class ActiveTripsFragmentListAdapter(
        private val values: ArrayList<Any?>,
        private val mode: Int?)
    : RecyclerView.Adapter<ActiveTripsFragmentListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_active_trip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (mode == 0) {
            val breakdown = values[position] as Breakdown
            holder.imRed.visibility = View.GONE
            holder.imGreen.visibility = View.VISIBLE
            holder.tvLocationDescription.text = breakdown.address
            holder.tvLocationCounter.text = breakdown.count.toString()
        } else {
            val congestion = values[position] as Congestions
            holder.imRed.visibility = View.VISIBLE
            holder.imGreen.visibility = View.GONE
            holder.tvLocationDescription.text = congestion.startLocation.address
            holder.tvLocationCounter.text = congestion.count.toString()
        }


//        holder.tvLocationCounter.text = item.content
    }

    fun addData(list: List<Any?>) {
        values.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imRed: ImageView = view.findViewById(R.id.imRed)
        val imGreen: ImageView = view.findViewById(R.id.imGreen)
        val tvLocationDescription: TextView = view.findViewById(R.id.tvLocationDescription)
        val tvLocationCounter: TextView = view.findViewById(R.id.tvLocationCounter)
    }
}