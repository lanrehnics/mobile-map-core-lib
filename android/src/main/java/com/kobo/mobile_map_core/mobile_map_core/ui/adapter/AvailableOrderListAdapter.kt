package com.kobo.mobile_map_core.mobile_map_core.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.models.orders.Orders
import kotlinx.android.synthetic.main.item_available_order.view.*
import java.util.*

class AvailableOrderListAdapter(private val availableOrderList: ArrayList<Orders?>, val itemClickListener: OnAvailableOrderItemClickListener) : RecyclerView.Adapter<AvailableOrderListAdapterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableOrderListAdapterHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_available_order, parent, false)
        return AvailableOrderListAdapterHolder(view)
    }

    override fun onBindViewHolder(holder: AvailableOrderListAdapterHolder, position: Int) = holder.bind(availableOrderList[position], itemClickListener)


    fun addData(list: List<Orders>) {
        availableOrderList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = availableOrderList.size
}


class AvailableOrderListAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(orders: Orders?, clickListener: OnAvailableOrderItemClickListener) {

        itemView.setOnClickListener {
            orders?.let { it1 -> clickListener.onItemClicked(it1) }
        }
        itemView.tvCustomerName.text = orders!!.customerName
        itemView.tvAssetInfo.text = "${orders.assetClass?.type} ${orders.assetClass?.size} ${orders.assetClass?.unit}"
        itemView.tvEta.text = orders.expectedETA?.text
        itemView.tvTotalOrders.text = ""
        itemView.tvPickUpLocationName.text = orders.pickupLocation?.address
        itemView.tvPickUpAddress.text = orders.pickupLocation?.address
    }
}

interface OnAvailableOrderItemClickListener {
    fun onItemClicked(order: Orders)
}