package com.kobo.mobile_map_core.mobile_map_core.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.kobo.mobile_map_core.mobile_map_core.ui.fragments.ActiveTripsFragmentList

class ActiveTripsPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    // Returns total number of pages
    override fun getCount(): Int {
        return NUM_ITEMS
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            ActiveTripsFragmentList.newInstance(0)
        } else {
            ActiveTripsFragmentList.newInstance(1)
        }
    }


    // Returns the page title for the top indicator
    override fun getPageTitle(position: Int): CharSequence? {
        return "Page $position"
    }

    companion object {
        private const val NUM_ITEMS = 2
    }
}