package com.dicoding.wanderlust.repository

import com.dicoding.wanderlust.data.pref.UserPreference
import com.dicoding.wanderlust.remote.retrofit.ApiService

class Repository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, userPreference)
            }.also { instance = it }
    }
}