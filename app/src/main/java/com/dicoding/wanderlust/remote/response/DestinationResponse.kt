package com.dicoding.wanderlust.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class DestinationResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
)

@Parcelize
data class DataItem(

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("openHour")
	val openHour: String? = null,

	@field:SerializedName("placeId")
	val placeId: Int? = null,

	@field:SerializedName("rating")
	val rating: Double? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("kelurahan")
	val kelurahan: String? = null,

	@field:SerializedName("long")
	val lon: Double? = null,

	@field:SerializedName("kecamatan")
	val kecamatan: String? = null,

	@field:SerializedName("location")
	val location: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("placeName")
	val placeName: String? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
) : Parcelable
