package com.kobo.mobile_map_core.mobile_map_core.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

class ActiveTripsDetailsPagerAdapter(fragmentManager: FragmentManager,
                                     private val pages: MutableList<Fragment>
//                                     private val fragmentTripDetails: FragmentTripDetails,
//                                     private val fragmentActiveTripEvents: FragmentActiveTripEvents
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    // Returns total number of pages

    override fun getCount(): Int {
        return pages.size
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): Fragment {

        return pages[position];


//        return if (position == 0) {
//            fragmentTripDetails
//        } else {
//            fragmentActiveTripEvents
//        }
    }


    // Returns the page title for the top indicator
    override fun getPageTitle(position: Int): CharSequence? {
        return "Page $position"
    }
}