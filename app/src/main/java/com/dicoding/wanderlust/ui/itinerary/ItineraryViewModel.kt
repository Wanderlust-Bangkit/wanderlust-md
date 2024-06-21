package com.dicoding.wanderlust.ui.itinerary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.data.model.Itinerary
import com.dicoding.wanderlust.data.model.Plan
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

    private val _createItineraryResult = MutableLiveData<ResultState<CommonResponse>>()
    val createItineraryResult: LiveData<ResultState<CommonResponse>> get() = _createItineraryResult

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
        plansItems: List<PlanItem>
    ) {
        _createItineraryResult.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val plans = plansItems.map { planItem ->
                    planItem.day?.let { Plan(day = it, destinations = planItem.destinations) }
                }

                val itinerary = Itinerary(
                    nameItenarary = name,
                    endDate = endDate,
                    location = location,
                    userId = userId,
                    plan = plans,
                    startDate = startDate
                )
                val response = repository.createItinerary(itinerary)
                _createItineraryResult.value = ResultState.Success(response)
            } catch (e: Exception) {
                _createItineraryResult.value = ResultState.Error(e.toString())
            }
        }
    }

}



