package com.dicoding.wanderlust.repository

import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.data.model.UserModel
import com.dicoding.wanderlust.data.pref.UserPreference
import com.dicoding.wanderlust.remote.response.CommonResponse
import com.dicoding.wanderlust.remote.response.DestinationResponse
import com.dicoding.wanderlust.remote.response.LoginResponse
import com.dicoding.wanderlust.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException

class Repository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun register(name: String, email: String, password: String
    ): Flow<ResultState<CommonResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val response = apiService.register(name, email, password)

            if (response.error == true) {
                emit(ResultState.Error(response.message.toString()))
            } else {
                emit(ResultState.Success(response))
            }

        } catch (exc: HttpException) {
            val errorBody = exc.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, CommonResponse::class.java)
            emit(ResultState.Error(errorResponse.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun login( email: String, password: String
    ): Flow<ResultState<LoginResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val response = apiService.login(email, password)

            if (response.error == true) {
                emit(ResultState.Error(response.message.toString()))
            } else {
                emit(ResultState.Success(response))
            }

        } catch (exc: HttpException) {
            val errorBody = exc.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(ResultState.Error(errorResponse.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun getAllDestinations(): DestinationResponse {
        return apiService.getAllDestinations()
    }

    suspend fun findDestination(keyword: String): DestinationResponse {
        if (keyword.isEmpty()){ getAllDestinations() }
        return apiService.findDestination(keyword)
    }

    suspend fun getDestinationByCategory(category: String): DestinationResponse {
        return apiService.findDestination(category)
    }

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