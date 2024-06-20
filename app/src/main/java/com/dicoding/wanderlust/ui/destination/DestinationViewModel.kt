package com.dicoding.wanderlust.ui.destination

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.data.model.UserModel
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

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _userSession = MutableLiveData<UserModel>()

    private var currentDestinationId: String? = null

    init {
        viewModelScope.launch {
            repository.getSession().collect { user ->
                _userSession.postValue(user)
            }
        }
    }

    fun searchDestinations(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _destinationList.postValue(ResultState.Loading)
            try {
                val response = repository.findDestination(keyword)
                response.data?.let {
                    _destinationList.postValue(ResultState.Success(it.filterNotNull()))
                } ?: run {
                    _destinationList.postValue(ResultState.Error("No data available for \"$keyword\""))
                }
            } catch (e: Exception) {
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
                    _destinationList.postValue(ResultState.Success(it.filterNotNull()))
                } ?: run {
                    _destinationList.postValue(ResultState.Error("No data available for \"$category\""))
                }
            } catch (e: Exception) {
                _destinationList.postValue(ResultState.Error(e.message ?: "Unknown error"))
            }
        }
    }

    private fun addFavorite(userId: String, destinationId: String) {
        currentDestinationId = destinationId
        viewModelScope.launch {
            repository.addFavorite(userId, destinationId).collect { resultState ->
                if (resultState is ResultState.Error && resultState.error.contains("Token expired")) {
                    _favoriteResult.postValue(ResultState.Error("Token expired, please login again"))
                    repository.logout()
                } else {
                    _favoriteResult.postValue(resultState)
                }
            }
        }
    }

    private fun deleteFavorite(userId: String, destinationId: String) {
        currentDestinationId = destinationId
        viewModelScope.launch {
            repository.deleteFavorite(userId, destinationId).collect { resultState ->
                if (resultState is ResultState.Error && resultState.error.contains("Token expired")) {
                    _favoriteResult.postValue(ResultState.Error("Token expired, please login again"))
                    repository.logout()
                } else {
                    _favoriteResult.postValue(resultState)
                }
            }
        }
    }

    fun toggleFavorite(destinationId: String) {
        currentDestinationId = destinationId
        val userId = _userSession.value?.userId
        if (userId != null) {
            if (_isFavorite.value == true) {
                deleteFavorite(userId, destinationId)
                _isFavorite.postValue(false)
            } else {
                addFavorite(userId, destinationId)
                _isFavorite.postValue(true)
            }
        } else {
            _favoriteResult.postValue(ResultState.Error("User not logged in"))
        }
    }

    fun checkIfFavorite(destinationId: String) {
        val userId = _userSession.value?.userId
        if (userId != null) {
            viewModelScope.launch {
                val favoriteStatus = repository.isFavorite(userId, destinationId)
                _isFavorite.postValue(favoriteStatus)
            }
        } else {
            _isFavorite.postValue(false)
        }
    }
}




