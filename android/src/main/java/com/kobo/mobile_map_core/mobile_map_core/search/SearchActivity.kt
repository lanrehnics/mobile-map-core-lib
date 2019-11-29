package com.kobo.mobile_map_core.mobile_map_core.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.kobo.mobile_map_core.mobile_map_core.R
import com.kobo.mobile_map_core.mobile_map_core.models.BattleFieldSearch
import com.kobo.mobile_map_core.mobile_map_core.models.SearchTag
import com.kobo.mobile_map_core.mobile_map_core.models.TruckModelDataParser
import java.util.*


class SearchActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SearchActivity ::: ===>"
    }


    private lateinit var autoCompleteSearch: AutoCompleteTextView
    private var truckIdStoreSearchEventListener: ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        autoCompleteSearch = findViewById(R.id.autoCompleteTextView)

        autoCompleteSearch.addTextChangedListener(mTextWatcher)

        autoCompleteSearch.onItemClickListener =
                AdapterView.OnItemClickListener { parent, _, position, _ ->
                    Toast.makeText(this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show()
                }


    }

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        private var timer = Timer()
        private val DELAY: Long = 1000

        override fun afterTextChanged(s: Editable?) {
            timer.cancel()
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    searchEverything(s.toString())

                }
            }, 1000)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    private fun searchEverything(searchWord: String) {
        try {


            val listOfSearchResults = mutableListOf<BattleFieldSearch>()

//            truckIdStoreSearchEventListener = db.collection("Trucks").whereEqualTo("d.").addSnapshotListener { querySnapshot, exception ->
//                if (exception != null) {
////                    showMessage(this, exception.toString())
//                }
//                adapter.clear()
//
//                val allIds = querySnapshot!!.documents.map { snapShot -> snapShot.id }.toSet().toList()
//                adapter.addAll(allIds)
//
//                autoCompleteSearch.setAdapter(adapter)
//                autoCompleteSearch.onItemClickListener =
//                        AdapterView.OnItemClickListener { parent, _, position, _ ->
//                            //                            focusOnTruck(parent.getItemAtPosition(position).toString())
//                        }
//                truckIdStoreSearchEventListener?.remove()
//            }


            // Search for truck with id reg_number first
            db.collection("Trucks")
                    .whereEqualTo("d.reg_number", searchWord)
                    .get()
                    .addOnSuccessListener { documents ->
                        documents.map { m -> BattleFieldSearch(1,m.toObject(TruckModelDataParser::class.java), SearchTag.REG_NUMBER) }.toList().forEach { r ->
                            listOfSearchResults.add(r)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                    }


            // Search for truck with trip id afterwards
            db.collection("Trucks")
                    .whereEqualTo("d.tripId", searchWord)
                    .get()
                    .addOnSuccessListener { documents ->
                        documents.map { m -> BattleFieldSearch(1,m.toObject(TruckModelDataParser::class.java), SearchTag.TRIP_ID) }.toList().forEach { r ->
                            listOfSearchResults.add(r)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                    }

//            if (listOfSearchResults.isNotEmpty()) {

            val poisArray = listOf(
                    BattleFieldSearch(1,"Taco Bell", "Athens", SearchTag.TRIP_ID),
                    BattleFieldSearch(2,"McDonald's", "Athens", SearchTag.PLACES),
                    BattleFieldSearch(3,"KFC", "Piraeus", SearchTag.PLACES),
                    BattleFieldSearch(4,"Shell", "Lamia", SearchTag.REG_NUMBER),
                    BattleFieldSearch(5,"BP", "Thessaloniki", SearchTag.REG_NUMBER)
            )

            val adapter = CustomSearchAdapter(this, R.layout.custom_search_item, poisArray)
            autoCompleteSearch.setAdapter(adapter)
            autoCompleteSearch.showDropDown()


        } catch (e: Exception) {
//            showMessage(this, "Error fetching truck info")
        }

    }

}
