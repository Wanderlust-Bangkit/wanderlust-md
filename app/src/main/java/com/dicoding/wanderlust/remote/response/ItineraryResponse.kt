package com.dicoding.wanderlust.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ItineraryResponse(

	@field:SerializedName("data")
	val data: List<ItineraryItem?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
)


@Parcelize
data class ItineraryItem(

	@field:SerializedName("nameItenarary")
	val nameItinerary: String? = null,

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("location")
	val location: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("plan")
	val plan: List<PlanItem>? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null
) : Parcelable

@Parcelize
data class PlanItem(

	@field:SerializedName("destinations")
	val destinations: List<DataItem>? = null,

	@field:SerializedName("day")
	val day: Int? = null
) : Parcelable
