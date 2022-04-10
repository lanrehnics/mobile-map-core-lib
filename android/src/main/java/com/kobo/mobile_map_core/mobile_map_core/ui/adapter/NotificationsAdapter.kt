package com.kobo.mobile_map_core.mobile_map_core.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.models.notifications.GeoNotification
import kotlinx.android.synthetic.main.item_notification.view.*
import java.text.SimpleDateFormat
import java.util.*

class NotificationsAdapter(private val values: ArrayList<GeoNotification?>, val itemClickListener: OnNotificationItemClickListener) : RecyclerView.Adapter<NotificationsAdapterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsAdapterHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notification, parent, false)
        return NotificationsAdapterHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationsAdapterHolder, position: Int) = holder.bind(values[position], itemClickListener)


    fun addData(list: List<GeoNotification>) {
        values.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = values.size
}


class NotificationsAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(notification: GeoNotification?, clickListener: OnNotificationItemClickListener) {
        itemView.notiTitle.text = notification?.title ?: "N/A"
        itemView.notiBody.text = notification?.message ?: "N/A"

        val DATE_FORMAT_I = "yyyy-MM-dd'T'HH:mm:ss"
        val DATE_FORMAT_O = "dd MMM, yyyy"


        val formatInput = SimpleDateFormat(DATE_FORMAT_I)
        val formatOutput = SimpleDateFormat(DATE_FORMAT_O)

        val date: Date = formatInput.parse(notification?.date)
        val dateString: String = formatOutput.format(date)

        itemView.notiTime.text = dateString




        itemView.setOnClickListener {
            clickListener.onItemClicked(notification)
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

interface OnNotificationItemClickListener {
    fun onItemClicked(notification: GeoNotification?)
}