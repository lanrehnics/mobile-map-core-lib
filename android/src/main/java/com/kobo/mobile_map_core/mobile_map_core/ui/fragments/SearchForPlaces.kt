package com.kobo.mobile_map_core.mobile_map_core.ui.fragments

import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.Autocomplete
import SelectedAddrss
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kobo.mobile_map_core.mobile_map_core.MobileMapCorePlugin
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiHelper
import com.kobo.mobile_map_core.mobile_map_core.data.api.ApiServiceImpl
import com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview.CustomerLocations
import com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview.Overview
import com.kobo.mobile_map_core.mobile_map_core.data.models.location_overview.OverviewData
import com.kobo.mobile_map_core.mobile_map_core.ui.base.ViewModelFactory
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.AutoCompleteAdapter
import com.kobo.mobile_map_core.mobile_map_core.ui.adapter.OnAutoCompleteItemClickListener
import com.kobo.mobile_map_core.mobile_map_core.ui.map.NewBaseMapActivity
import com.kobo.mobile_map_core.mobile_map_core.ui.viewmodel.SearchForPlacesViewModel
import com.xdev.mvvm.utils.Status
import iammert.com.expandablelib.ExpandCollapseListener.CollapseListener
import iammert.com.expandablelib.ExpandCollapseListener.ExpandListener
import iammert.com.expandablelib.ExpandableLayout
import iammert.com.expandablelib.Section
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"


class SearchForPlaces : Fragment(), OnAutoCompleteItemClickListener, CoroutineScope {

    private lateinit var callback: OnAddressSelectedListener
    override val coroutineContext: CoroutineContext = Dispatchers.Main

    fun setOnAddressSelectedListener(callback: OnAddressSelectedListener) {
        this.callback = callback
    }

    private var mode: Int? = 0
    private var param2: String? = null
    private var overview: Overview? = null


    private lateinit var searchForPlacesViewModel: SearchForPlacesViewModel
    private lateinit var suggestedPlacesRecyclerView: RecyclerView
    private lateinit var expandableLayout: ExpandableLayout
    private lateinit var linearWhereIAmGoing: LinearLayout
    private lateinit var imageViewCircle: ImageView
    private lateinit var chevrondown: ImageView
    private lateinit var chevronside: ImageView
    private lateinit var whereGoingToEditText: EditText
    private lateinit var whereIam: EditText
    private lateinit var whereIamTextWatcher: TextWatcher
    private lateinit var whereGoingToEditTextWatcher: TextWatcher
    private lateinit var progressWhereIAm: ProgressBar
    private lateinit var progressWhereGoing: ProgressBar


    //    private lateinit var listView: ListView
    private lateinit var adapter: AutoCompleteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { it ->
            mode = it.getInt(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            overview = it.getSerializable(ARG_PARAM3)?.let { it as Overview }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_search_for_places, container, false)
        suggestedPlacesRecyclerView = view.findViewById(R.id.suggestedPlacesRecyclerView)
        imageViewCircle = view.findViewById(R.id.imageViewCircle)
        whereGoingToEditText = view.findViewById(R.id.whereGoingToEditText)
        whereIam = view.findViewById(R.id.whereIam)
        linearWhereIAmGoing = view.findViewById(R.id.linearWhereIAmGoing)
        progressWhereIAm = view.findViewById(R.id.progressWhereIAm)
        progressWhereGoing = view.findViewById(R.id.progressWhereGoing)


        setUpTextWatcher()

        whereIam.addTextChangedListener(whereIamTextWatcher)
        whereGoingToEditText.addTextChangedListener(whereGoingToEditTextWatcher)



        configureViewMode()

        setUpExpandableView(view)
        setupUI()
        setupViewModel()
        return view
    }


    private fun setUpTextWatcher() {

        whereIamTextWatcher = object : TextWatcher {
            private var searchFor = ""

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                if (searchText == searchFor)
                    return

                searchFor = searchText

                if (linearWhereIAmGoing.visibility == View.GONE) {
                    // The logic is on entering this class mode is set from outside,
                    // but on getting here since the editText for current location is set to previously selected or current user location,
                    // The mode will change as this activate textWatcher,

                    // So, if user is coming to select destination the linearWhereIAmGoing view should ve visible
                    // if visible, then the mode shouldn't change
                    mode = 0
                }

                launch {
                    delay(300)  //debounce timeOut
                    if (searchText != searchFor)
                        return@launch

                    placesAutoComplete(searchText)
                    // do our magic here
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        }
        whereGoingToEditTextWatcher = object : TextWatcher {
            private var searchFor = ""

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                if (searchText == searchFor)
                    return

                searchFor = searchText
                mode = 1

                launch {
                    delay(300)  //debounce timeOut
                    if (searchText != searchFor)
                        return@launch
                    placesAutoComplete(searchText)

                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int, param2: String?, param3: Overview?) =
                SearchForPlaces().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                        putSerializable(ARG_PARAM3, param3)
                    }
                }
    }


    private fun placesAutoComplete(searchTerm: String) {
        searchForPlacesViewModel.getPlaces(searchTerm).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    if (mode == 0) {
                        progressWhereIAm.visibility = View.GONE
                    } else {
                        progressWhereGoing.visibility = View.GONE
                    }
                    it.data?.let { autoComplete -> renderList(autoComplete.data.autocomplete) }
                }
                Status.LOADING -> {
                    if (mode == 0) {
                        progressWhereIAm.visibility = View.VISIBLE
                    } else {
                        progressWhereGoing.visibility = View.VISIBLE
                    }
                }
                Status.ERROR -> {
                    if (mode == 0) {
                        progressWhereIAm.visibility = View.GONE
                    } else {
                        progressWhereGoing.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun configureViewMode() {
        when (mode) {
            0 -> {
                linearWhereIAmGoing.visibility = View.GONE
            }
            else -> {
                whereIam.text = param2?.toEditable()
                linearWhereIAmGoing.visibility = View.VISIBLE
            }
        }
    }

    private fun setupUI() {
        suggestedPlacesRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = AutoCompleteAdapter(arrayListOf(), this)
        suggestedPlacesRecyclerView.addItemDecoration(
                DividerItemDecoration(
                        suggestedPlacesRecyclerView.context,
                        (suggestedPlacesRecyclerView.layoutManager as LinearLayoutManager).orientation
                )
        )
        suggestedPlacesRecyclerView.adapter = adapter
    }


    private fun renderList(results: List<Autocomplete>?) {
        adapter.addData(results)
        adapter.notifyDataSetChanged()
    }

    private fun setupViewModel() {
        searchForPlacesViewModel = ViewModelProviders.of(
                this,
                ViewModelFactory(ApiHelper(ApiServiceImpl( requireActivity())))
        ).get(SearchForPlacesViewModel::class.java)
    }


    private fun setUpExpandableView(view: View) {

        expandableLayout = view.findViewById(R.id.el)

        expandableLayout.setRenderer(object : ExpandableLayout.Renderer<Map<String, String>, Autocomplete> {
            override fun renderParent(view: View, model: Map<String, String>, isExpanded: Boolean, parentPosition: Int) {
                (view.findViewById<View>(R.id.tvSavedLocation) as TextView).text = model["title"]
                (view.findViewById<View>(R.id.tvSavedLocationCounts) as TextView).text = model["counts"]
            }

            override fun renderChild(view: View, model: Autocomplete, parentPosition: Int, childPosition: Int) {
                (view.findViewById<View>(R.id.tvPlaces) as TextView).text = model.description
                (view.findViewById<View>(R.id.tvDescription) as TextView).text = model.description

                view.setOnClickListener {
                    when (mode) {
                        0 -> {
                            callback.onSelect(SelectedAddrss(model, null, mode))
                        }
                        else -> {
                            callback.onSelect(SelectedAddrss(null, model, mode))
                        }
                    }
                }
            }
        })

        expandableLayout.setExpandListener(ExpandListener<Any?> { parentIndex, parent, view ->
            (view.findViewById(R.id.chevrondown) as ImageView).visibility = View.VISIBLE
            (view.findViewById(R.id.chevronside) as ImageView).visibility = View.GONE
        })

        expandableLayout.setCollapseListener(CollapseListener<Any?> { parentIndex, parent, view ->
            (view.findViewById(R.id.chevrondown) as ImageView).visibility = View.GONE
            (view.findViewById(R.id.chevronside) as ImageView).visibility = View.VISIBLE
        })

        overview?.customerLocations?.let {
            val section: Section<Map<String, String>, Autocomplete> = Section()
            section.parent = mutableMapOf("title" to "Saved Locations", "counts" to it.size.toString())
            it.forEach { customerLocation ->
                section.children.add(Autocomplete(customerLocation?.locationName, "", null, NewBaseMapActivity.toLatLng(customerLocation?.location?.coordinates)))
            }
            expandableLayout.addSection(section)
        }


    }

    override fun onItemClicked(address: Autocomplete) {
        when (mode) {
            0 -> {
                callback.onSelect(SelectedAddrss(address, null,mode))
            }
            else -> {
                callback.onSelect(SelectedAddrss(null, address, mode))
            }
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    interface OnAddressSelectedListener {
        fun onSelect(selectedAddress: SelectedAddrss)
    }

}