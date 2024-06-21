package com.dicoding.wanderlust.data.model

import com.dicoding.wanderlust.remote.response.DataItem

data class Plan(
    val day: Int,
    val destinations: List<DataItem>?
)

data class Itinerary(
    val nameItenarary: String,
    val endDate: String,
    val location: String,
    val userId: String,
    val plan: List<Plan?>,
    val startDate: String
)
