package com.dicoding.wanderlust.data.model

import com.dicoding.wanderlust.remote.response.DataItem

data class ItineraryModel(
    val id: String,
    val endDate: String,
    val name: String,
    val destination: String,
    val userId: String,
    val startDate: String,
    val listDestination: Map<String, List<DataItem>>
)