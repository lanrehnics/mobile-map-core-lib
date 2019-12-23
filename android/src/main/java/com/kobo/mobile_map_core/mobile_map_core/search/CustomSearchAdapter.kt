package com.kobo.mobile_map_core.mobile_map_core.search

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.models.BattleFieldSearch
import java.util.*


class CustomSearchAdapter(context: Context, @LayoutRes private val layoutResource: Int, private val allSearchResults: List<BattleFieldSearch>) :
        ArrayAdapter<BattleFieldSearch>(context, layoutResource, allSearchResults),
        Filterable {
    private var mResults: List<BattleFieldSearch> = allSearchResults

    override fun getCount(): Int {
        return mResults.size
    }

    override fun getItem(p0: Int): BattleFieldSearch? {
        return mResults[p0]

    }


    override fun getItemId(p0: Int): Long {
        return mResults[p0].id.toLong()
    }




    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            v = LayoutInflater.from(parent.context)
                    .inflate(layoutResource, parent, false)
        }
        val strName = v!!.findViewById<View>(R.id.search_item) as TextView
        strName.text = "${mResults[position].regNumber} ${mResults[position].tripId} (${mResults[position].tag})"
        return v
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                mResults = filterResults.values as List<BattleFieldSearch>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase(Locale.getDefault())

                val filterResults = FilterResults()
                filterResults.values = if (queryString == null || queryString.isEmpty())
                    allSearchResults
                else
                    allSearchResults.filter {
                        it.regNumber.toLowerCase(Locale.getDefault()).contains(queryString) ||
                                it.tripId.toLowerCase(Locale.getDefault()).contains(queryString)
                    }

                return filterResults
            }

        }
    }
}