package com.dicoding.wanderlust.data.di

import android.content.Context
import com.dicoding.wanderlust.data.pref.UserPreference
import com.dicoding.wanderlust.data.pref.dataStore
import com.dicoding.wanderlust.remote.retrofit.ApiConfig
import com.dicoding.wanderlust.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return Repository.getInstance(apiService, pref)
    }
}