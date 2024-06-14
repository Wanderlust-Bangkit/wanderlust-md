package com.dicoding.wanderlust.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.remote.response.CommonResponse
import com.dicoding.wanderlust.repository.Repository
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: Repository) : ViewModel() {

    private val _registerResult = MutableLiveData<ResultState<CommonResponse>>()
    val registerResult: LiveData<ResultState<CommonResponse>> = _registerResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            repository.register(name, email, password).collect {
                _registerResult.value = it
            }
        }
    }

}