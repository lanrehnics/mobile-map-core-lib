package com.kobo.mobile_map_core.mobile_map_core.ui.adapter

import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.Autocomplete
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.kobo.mobile_map_core.mobile_map_core.R

class SaveLocationAdapter(private val context: FragmentActivity?,
                          private val dataSource: ArrayList<Autocomplete>) : BaseAdapter() {

    private val inflater: LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return dataSource.size
    }

    //2
    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    //3
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //4
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.item_saved_places, parent, false)
        val tvPlaces = rowView.findViewById(R.id.tvPlaces) as TextView
        val tvDescription = rowView.findViewById(R.id.tvDescription) as TextView

        val places = getItem(position) as Autocomplete

        tvPlaces.text = places.description
        tvDescription.text = places.description



        return rowView
    }
}