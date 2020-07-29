import com.google.gson.annotations.SerializedName
import com.kobo.mobile_map_core.mobile_map_core.data.models.autocomplete.Autocomplete

data class AutoCompleteData (
		@SerializedName("autocomplete") val autocomplete : List<Autocomplete>
)