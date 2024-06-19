package com.dicoding.wanderlust.ui.destination

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.remote.response.DataItem
import com.dicoding.wanderlust.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DestinationViewModel(private val repository: Repository) : ViewModel() {
    private val _destinationList = MutableLiveData<ResultState<List<DataItem>>>()
    val destinationList: LiveData<ResultState<List<DataItem>>> = _destinationList

    fun getDestinations() {
        viewModelScope.launch(Dispatchers.IO) {
            _destinationList.postValue(ResultState.Loading)
            try {
                val response = repository.getAllDestinations()
                response.data?.let {
                    Log.d("DestinationViewModel", "Data received: $it")
                    _destinationList.postValue(ResultState.Success(it.filterNotNull()))
                } ?: run {
                    Log.d("DestinationViewModel", "No data available")
                    _destinationList.postValue(ResultState.Error("No data available"))
                }
            } catch (e: Exception) {
                Log.e("DestinationViewModel", "Exception occurred", e)
                _destinationList.postValue(ResultState.Error(e.message ?: "Unknown error"))
            }
        }
    }

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
}



