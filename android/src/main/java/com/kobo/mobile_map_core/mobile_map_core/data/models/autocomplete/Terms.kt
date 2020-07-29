import com.google.gson.annotations.SerializedName

data class Terms (

		@SerializedName("offset") val offset : Int,
		@SerializedName("value") val value : String
)