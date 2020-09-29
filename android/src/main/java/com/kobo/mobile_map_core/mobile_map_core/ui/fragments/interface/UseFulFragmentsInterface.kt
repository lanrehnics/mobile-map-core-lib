package com.kobo.mobile_map_core.mobile_map_core.ui.fragments.`interface`

import com.kobo.mobile_map_core.mobile_map_core.data.models.dedicatedtrucks.Trucks

class UseFulFragmentsInterface {

    interface OnInfoClickedListener {
        fun onSelect(selectedInfo: Any)
    }

    interface OnCloseButtonClickListener {
        fun onTripInfoCloseButtonClick()
    }

    interface SwitchToMapClickListener {
        fun onSwitchToMapClickListener()
    }
}