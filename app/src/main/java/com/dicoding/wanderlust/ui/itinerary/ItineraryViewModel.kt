package com.dicoding.wanderlust.ui.itinerary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.remote.response.CommonResponse
import com.dicoding.wanderlust.remote.response.DestinationResponse
import com.dicoding.wanderlust.remote.response.ItineraryItem
import com.dicoding.wanderlust.remote.response.PlanItem
import com.dicoding.wanderlust.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItineraryViewModel(private val repository: Repository) : ViewModel() {

    private val _itineraryList = MutableLiveData<ResultState<List<ItineraryItem>>>()
    val itineraryList: LiveData<ResultState<List<ItineraryItem>>> = _itineraryList

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    init {
        getUserId()
        observeUserId()
    }

    private fun getUserId() {
        viewModelScope.launch {
            repository.getSession().collect { user ->
                _userId.postValue(user.userId)
            }
        }
    }

    private fun observeUserId() {
        userId.observeForever { userId ->
            userId?.let {
                getAllItineraries(userId)
            }
        }
    }

    private fun getAllItineraries(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("ItineraryViewModel", "Fetching itineraries for userId: $userId")
            _itineraryList.postValue(ResultState.Loading)
            try {
                val response = repository.getAllItineraries(userId)
                response.data?.let {
                    Log.d("ItineraryViewModel", "Itineraries fetched: ${it.size}")
                    _itineraryList.postValue(ResultState.Success(it.filterNotNull()))
                } ?: run {
                    Log.e("ItineraryViewModel", "No data available for \"$userId\"")
                    _itineraryList.postValue(ResultState.Error("No data available for \"$userId\""))
                }
            } catch (e: Exception) {
                Log.e("ItineraryViewModel", "Error fetching itineraries: ${e.message}")
                _itineraryList.postValue(ResultState.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun generateItinerary(category: String, location: String): LiveData<ResultState<DestinationResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = repository.generateItinerary(category, location)
            Log.d("Suggestion", "generateItinerary response: $response")
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            Log.e("Suggestion", "Error generating itinerary: ${e.message}")
            emit(ResultState.Error(e.toString()))
        }
    }

    fun createItinerary(
        name: String,
        location: String,
        startDate: String,
        endDate: String,
        userId: String,
        planItems: List<PlanItem>
    ): LiveData<ResultState<CommonResponse>> = liveData {
        Log.d("ItineraryViewModel", "Creating itinerary with parameters:")
        Log.d("ItineraryViewModel", "Name: $name")
        Log.d("ItineraryViewModel", "Location: $location")
        Log.d("ItineraryViewModel", "Start Date: $startDate")
        Log.d("ItineraryViewModel", "End Date: $endDate")
        Log.d("ItineraryViewModel", "User ID: $userId")
        Log.d("ItineraryViewModel", "Plan Items: $planItems")

        emit(ResultState.Loading)
        try {
            val response = repository.createItinerary(name, location, startDate, endDate, userId, planItems)
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            emit(ResultState.Error(e.toString()))
        }
    }

}



