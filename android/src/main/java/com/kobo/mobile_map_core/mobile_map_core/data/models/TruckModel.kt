package com.kobo.mobile_map_core.mobile_map_core.data.models

object TripStatus {
    const val STATUS_PENDING: String = "Pending"
    const val STATUS_POSITIONED: String = "Positioned"
    const val STATUS_IN_PREMISE: String = "In-premise"
    const val STATUS_LOADED: String = "Loaded"
    const val STATUS_TRANSPORTING: String = "Transporting"
    const val STATUS_AT_DESTINATION: String = "At-destination"
    const val STATUS_DELIVERED: String = "Delivered"
    const val STATUS_AVAILABLE: String = "Available"
    const val STATUS_WAYBILL_COLLECTED: String = "Waybill Collected"
    const val STATUS_ACTIVE_TRIPS: String = "Active"
    const val STATUS_FLAGGED_TRIP: String = "com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Flagged"

}

enum class DisplayMode {
    SINGLE,
    ALL
}

enum class SearchTag {
    REG_NUMBER,
    TRIP_ID,
    PLACES
}

enum class ClearCommand {
    MAP,
    DATA,
    WITHOUT_VIEW,
    ALL
}