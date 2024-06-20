package com.dicoding.wanderlust.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.wanderlust.repository.Repository
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: Repository) : ViewModel() {
    val user = repository.getSession().asLiveData()
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
