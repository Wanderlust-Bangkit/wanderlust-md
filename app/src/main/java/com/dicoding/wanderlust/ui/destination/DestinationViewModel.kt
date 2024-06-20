package com.dicoding.wanderlust.ui.destination

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.remote.response.CommonResponse
import com.dicoding.wanderlust.remote.response.DataItem
import com.dicoding.wanderlust.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DestinationViewModel(private val repository: Repository) : ViewModel() {
    private val _destinationList = MutableLiveData<ResultState<List<DataItem>>>()
    val destinationList: LiveData<ResultState<List<DataItem>>> = _destinationList

    private val _favoriteResult = MutableLiveData<ResultState<CommonResponse>>()
    val favoriteResult: LiveData<ResultState<CommonResponse>> = _favoriteResult

    private var currentDestinationId: String? = null


    fun searchDestinations(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _destinationList.postValue(ResultState.Loading)
            try {
                val response = repository.findDestination(keyword)
                response.data?.let {
                    Log.d("DestinationViewModel", "Data received: $it")
                    _destinationList.postValue(ResultState.Success(it.filterNotNull()))
                } ?: run {
                    Log.d("DestinationViewModel", "No data available for search keyword: $keyword")
                    _destinationList.postValue(ResultState.Error("No data available for \"$keyword\""))
                }
            } catch (e: Exception) {
                Log.e("DestinationViewModel", "Exception occurred", e)
                _destinationList.postValue(ResultState.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun getDestinationByCategory(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _destinationList.postValue(ResultState.Loading)
            try {
                val response = repository.getDestinationByCategory(category)
                response.data?.let {
                    Log.d("DestinationViewModel", "Data received: $it")
                    _destinationList.postValue(ResultState.Success(it.filterNotNull()))
                } ?: run {
                    Log.d("DestinationViewModel", "No data available for search keyword: $category")
                    _destinationList.postValue(ResultState.Error("No data available for \"$category\""))
                }
            } catch (e: Exception) {
                Log.e("DestinationViewModel", "Exception occurred", e)
                _destinationList.postValue(ResultState.Error(e.message ?: "Unknown error"))
            }
        }
    }

    private fun addFavorite(destinationId: String) {
        currentDestinationId = destinationId
        viewModelScope.launch {
            repository.addFavorite(destinationId).collect { resultState ->
                if (resultState is ResultState.Error && resultState.error.contains("Token expired")) {
                    // Handle token expired case
                    _favoriteResult.postValue(ResultState.Error("Token expired, please login again"))
                    // Navigate to login screen or perform logout
                    repository.logout()
                } else {
                    _favoriteResult.postValue(resultState)
                }
            }
        }
    }

    private fun deleteFavorite(destinationId: String) {
        currentDestinationId = destinationId
        viewModelScope.launch {
            repository.deleteFavorite(destinationId).collect { resultState ->
                if (resultState is ResultState.Error && resultState.error.contains("Token expired")) {
                    // Handle token expired case
                    _favoriteResult.postValue(ResultState.Error("Token expired, please login again"))
                    // Navigate to login screen or perform logout
                    repository.logout()
                } else {
                    _favoriteResult.postValue(resultState)
                }
            }
        }
    }

    fun toggleFavorite(destinationId: String) {
        currentDestinationId = destinationId
        if (isFavorite()) {
            deleteFavorite(destinationId)
        } else {
            addFavorite(destinationId)
        }
    }

    fun isFavorite(): Boolean {
        return false
    }
}



