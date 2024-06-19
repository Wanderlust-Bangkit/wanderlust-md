package com.dicoding.wanderlust.ui.itinerary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class ItineraryViewModel : ViewModel() {

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _selectedDestination = MutableLiveData<String>()
    val selectedDestination: LiveData<String> = _selectedDestination

    private val _startDate = MutableLiveData<Date>()
    val startDate: LiveData<Date> = _startDate

    private val _endDate = MutableLiveData<Date>()
    val endDate: LiveData<Date> = _endDate

    private val _destinations = MutableLiveData<List<String>>().apply {
        value = listOf("Destination 1", "Destination 2", "Destination 3") // Nanti hrsnya ambil dr db
    }
    val destinations: LiveData<List<String>> = _destinations

    fun setName(name: String) {
        _name.value = name
    }

    fun setSelectedDestination(destination: String) {
        _selectedDestination.value = destination
    }

    fun setStartDate(date: Date) {
        _startDate.value = date
    }

    fun setEndDate(date: Date) {
        _endDate.value = date
    }
}
