package com.dicoding.wanderlust.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.remote.response.DataItem
import com.dicoding.wanderlust.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: Repository) : ViewModel() {

    private val _destinationList = MutableLiveData<ResultState<List<DataItem>>>()
    val destinationList: LiveData<ResultState<List<DataItem>>> = _destinationList

    val user = repository.getSession().asLiveData()

    fun getAllFavorites(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _destinationList.postValue(ResultState.Loading)
            try {
                val response = repository.getAllFavorites(userId)
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
}
