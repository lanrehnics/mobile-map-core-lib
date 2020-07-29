import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.activetrips.Stops
import java.io.Serializable

data class WhiteListedStop(

        @SerializedName("type") val type: String,
        @SerializedName("coordinates") val coordinates: List<Double>,
        @SerializedName("address") val address: String,
        @SerializedName("geohash") val geohash: String,
        @SerializedName("stops") val stops: List<Stops>
): Serializable