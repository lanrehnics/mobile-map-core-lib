package com.kobo.mobile_map_core.mobile_map_core.ui.adapter

import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Events
import java.util.*
import kotlin.collections.ArrayList

class ActiveTripEventsListAdapter(
        private val values: ArrayList<Events?>)
    : RecyclerView.Adapter<ActiveTripEventsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_trip_event_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//        Drawable placeholder = holder.getContext().getResources().getDrawable(R.drawa
        val event = values[position]
        holder.imageTripEventIcon.setImageDrawable(event?.name?.let { setEquivalentEventDrawable(holder.imageTripEventIcon, it) })
        holder.tvTripEvent.text = event?.name
        holder.tvTripEventAddress.text = event?.location?.address
        holder.tvTripEventTime.text = event?.datetime


//        holder.tvLocationCounter.text = item.content
    }

    fun addData(list: List<Events?>) {
        values.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imageTripEventIcon: ImageView = view.findViewById(R.id.imageTripEventIcon)
        val tvTripEventAddress: TextView = view.findViewById(R.id.tvTripEventAddress)
        val tvTripEvent: TextView = view.findViewById(R.id.tvTripEvent)
        val tvTripEventTime: TextView = view.findViewById(R.id.tvTripEventTime)
    }

    private fun setEquivalentEventDrawable(view: View, event: String): Drawable? {
        return when (event.trim().toLowerCase(Locale.getDefault())) {
            "SHARP TURN".toLowerCase(Locale.getDefault()) -> view.context.getDrawable(R.drawable.ic_overspeeding)
            "OVERSPEEDING".toLowerCase(Locale.getDefault()) -> view.context.getDrawable(R.drawable.ic_overspeeding)
            "TRIP START".toLowerCase(Locale.getDefault()) -> view.context.getDrawable(R.drawable.ic_trip_started)
            "TRIP END".toLowerCase(Locale.getDefault()) -> view.context.getDrawable(R.drawable.ic_trip_end)
            "TRUCK BREAKDOWN".toLowerCase(Locale.getDefault()) -> view.context.getDrawable(R.drawable.ic_truck_breakdown)
            "POLICE STOP".toLowerCase(Locale.getDefault()) -> view.context.getDrawable(R.drawable.ic_truck_breakdown)
            "DROP OFF DELIVERY".toLowerCase(Locale.getDefault()) -> view.context.getDrawable(R.drawable.ic_drop_off_delivery)
            else -> view.context.getDrawable(R.drawable.ic_overspeeding)

        }

    }
}