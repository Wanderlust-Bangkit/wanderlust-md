package com.dicoding.wanderlust.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.remote.response.DataItem
import com.dicoding.wanderlust.repository.Repository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val _topRatedDestinations = MutableLiveData<ResultState<List<DataItem>>>()
    val topRatedDestinations: LiveData<ResultState<List<DataItem>>> = _topRatedDestinations

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    init {
        getTopRatedDestinations()
        getUserName()
    }

    private fun getTopRatedDestinations() {
        viewModelScope.launch {
            _topRatedDestinations.postValue(ResultState.Loading)
            try {
                val response = repository.getAllDestinations()
                val allDestinations = response.data ?: emptyList()
                val topRated = allDestinations.filterNotNull().sortedByDescending { it.rating ?: 0.0 }.take(5)
                _topRatedDestinations.postValue(ResultState.Success(topRated))
            } catch (e: Exception) {
                _topRatedDestinations.postValue(ResultState.Error(e.toString()))
            }
        }
    }

    private fun getUserName() {
        viewModelScope.launch {
            repository.getSession().collect { user ->
                _userName.postValue(user.name)
            }
        }
    }
}

