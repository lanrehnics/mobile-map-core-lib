package com.kobo.mobile_map_core.mobile_map_core.ui.fragments

import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.Autocomplete
import com.kobo.mobile_map_core.mobile_map_core.data.models.SelectedAddrss
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiHelper
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiServiceImpl
import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.AutoCompleteResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks
import com.kobo.mobile_map_core.mobile_map_core.data.models.gsearch.GeneralSearchResponse
import com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview.Overview
import com.kobo.mobile_map_core.mobile_map_core.ui.base.ViewModelFactory
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.AutoCompleteAdapter
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.GeneralSearchAdapter
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.OnAutoCompleteItemClickListener
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.OnSearchResultItemClickListener
import com.kobo.mobile_map_core.mobile_map_core.ui.map.NewBaseMapActivity
import com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel.GeneralSearchViewModel
import com.xdev.mvvm.utils.Status
import iammert.com.expandablelib.ExpandableLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

private const val ARG_PARAM = "param"


class GeneralSearchFragment : Fragment(), OnSearchResultItemClickListener, CoroutineScope {

    private lateinit var callback: OnResultItemClickListener
    override val coroutineContext: CoroutineContext = Dispatchers.Main

    fun setOnResultItemClickListener(callback: OnResultItemClickListener) {
        this.callback = callback
    }

    private var param: String? = null


    private lateinit var generalSearchViewModel: GeneralSearchViewModel
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var whereIam: EditText
    private lateinit var whereIamTextWatcher: TextWatcher
    private lateinit var whereGoingToEditTextWatcher: TextWatcher
    private lateinit var progress: ProgressBar


    private lateinit var adapter: GeneralSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { it ->
            param = it.getString(ARG_PARAM)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        setupViewModel()
        val view: View = inflater.inflate(R.layout.fragment_general_search, container, false)
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView)
        whereIam = view.findViewById(R.id.whereIam)
        progress = view.findViewById(R.id.progress)

        setUpTextWatcher()
        whereIam.addTextChangedListener(whereIamTextWatcher)
        setupUI()
        return view
    }


    private fun setUpTextWatcher() {

        whereIamTextWatcher = object : TextWatcher {
            private var searchFor: String? = ""

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText: String? = s.toString().trim()
                if (searchText == searchFor)
                    return

                searchFor = searchText

                launch {
                    delay(300)  //debounce timeOut
                    if (searchText != searchFor)
                        return@launch
                    val query = mutableMapOf("searchTerm" to searchText)
                    searchNow(query)
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param: String?) =
                SearchForPlaces().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM, param)
                    }
                }
    }


    private fun searchNow(query: MutableMap<String, String?>) {
        generalSearchViewModel.searchForData(query).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progress.visibility = View.GONE
                    it.data?.let { autoComplete -> renderList(autoComplete) }
                }
                Status.LOADING -> {
                    progress.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    progress.visibility = View.GONE
                }
            }
        })
    }

    private fun setupUI() {
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = GeneralSearchAdapter(arrayListOf(), this)
        searchResultsRecyclerView.addItemDecoration(
                DividerItemDecoration(
                        searchResultsRecyclerView.context,
                        (searchResultsRecyclerView.layoutManager as LinearLayoutManager).orientation
                )
        )
        searchResultsRecyclerView.adapter = adapter
    }


    private fun renderList(results: GeneralSearchResponse) {
        adapter.addData(results.data.result)
        adapter.notifyDataSetChanged()
    }

    private fun setupViewModel() {
        generalSearchViewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiServiceImpl(requireActivity())))
        ).get(GeneralSearchViewModel::class.java)
    }

    override fun onItemClicked(truck: Trucks) {
        callback.onSearchItemResultSelected(truck)
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    interface OnResultItemClickListener {
        fun onSearchItemResultSelected(truck: Trucks)
    }

}