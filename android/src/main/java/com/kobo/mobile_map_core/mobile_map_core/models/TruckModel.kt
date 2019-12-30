package com.kobo.mobile_map_core.mobile_map_core.models

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName

data class TruckModelDataParser(
        @PropertyName("d") val d: TruckModel = TruckModel(),
        @PropertyName("g") val g: String = "",
        @PropertyName("l") val l: GeoPoint = GeoPoint(0.0, 0.0)

)

data class BattleFieldSearch(var id: Int, var regNumber: String, var tripId: String, var tag: SearchTag) {
    constructor (id: Int, truckModel: TruckModelDataParser, tag: SearchTag) : this(id, truckModel.d.reg_number, truckModel.d.tripId, tag)
}


data class TruckModel(
        @PropertyName("lastConnectionTime") val lastConnectionTime: String = "",
        @PropertyName("reg_number") val reg_number: String = "",
        @PropertyName("routeTag") val routeTag: String = "",
//        @PropertyName("speed") val speed: Long = 0L,
        @PropertyName("bearing") val bearing: String = "0.0",
        @PropertyName("geohash") val geohash: String = "0.0",
        @PropertyName("lat") val lat: Double = 0.0,
        @PropertyName("lon") val lon: Double = 0.0,
        @PropertyName("active") val active: Int = 0,
        @PropertyName("admin") val admin: Int = 0,
        @PropertyName("flagged") val flagged: Boolean = false,
        @PropertyName("customerId") val customerId: Int = 0,
        @PropertyName("partnerId") val partnerId: Int = 0,
        @PropertyName("asset_class") val asset_class: String = "",
        @PropertyName("customerName") val customerName: String = "",
        @PropertyName("delivered") val delivered: Boolean = false,
        @PropertyName("destination") val destination: String = "",
        @PropertyName("destinationCountry") val destinationCountry: String = "",
        @PropertyName("driverName") val driverName: String = "",
        @PropertyName("locationAddress") val locationAddress: String = "",
        @PropertyName("driverMobile") val driverMobile: String = "",
        @PropertyName("goodCategory") val goodCategory: String = "",
        @PropertyName("goodType") val goodType: String = "",
        @PropertyName("recipient") val recipient: String = "",
        @PropertyName("source") val source: String = "",
        @PropertyName("status") val status: String = "",
        @PropertyName("transportStatus") val transportStatus: String = "",
        @PropertyName("tripId") val tripId: String = "",
        @PropertyName("sourceCountry") val sourceCountry: String = "",
        @PropertyName("deliveryStation") val deliveryStation: DeliveryStation = DeliveryStation(),
        @PropertyName("pickupStation") val pickUpStation: PickUpStation = PickUpStation(),
        @PropertyName("nextPosition") val nextPosition: NextPosition = NextPosition()
)

data class DeliveryStation(
        @PropertyName("_id") val id: String = "",
        @PropertyName("address") val address: String = "",
        @PropertyName("location") val location: KLocation = KLocation()
)

data class PickUpStation(
        @PropertyName("_id") val id: String = "",
        @PropertyName("address") val address: String = "",
        @PropertyName("location") val location: KLocation = KLocation()
)

data class KLocation(
        @PropertyName("coordinates") val coordinates: List<Double> = listOf(0.0, 0.0)
)

data class NextPosition(
        @PropertyName("latitude") val latitude: Double = 0.0,
        @PropertyName("longitude") val longitude: Double = 0.0
)

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
    const val STATUS_FLAGGED_TRIP: String = "Flagged"

}

class TripInfoWithMarker(val tripInfo: TruckModelDataParser)

//sealed class FilterWith {
//    class TripStatus(val status: String?) : FilterWith()
//    class ActiveTrips(val active: Int?) : FilterWith()
//    class FlaggedTrips(val flagged: Boolean?) : FilterWith()
//}

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