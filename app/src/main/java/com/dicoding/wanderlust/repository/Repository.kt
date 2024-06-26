package com.dicoding.wanderlust.repository

import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.data.model.Itinerary
import com.dicoding.wanderlust.data.model.UserModel
import com.dicoding.wanderlust.data.pref.UserPreference
import com.dicoding.wanderlust.remote.response.CommonResponse
import com.dicoding.wanderlust.remote.response.DestinationResponse
import com.dicoding.wanderlust.remote.response.ItineraryResponse
import com.dicoding.wanderlust.remote.response.LoginResponse
import com.dicoding.wanderlust.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Repository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    fun register(name: String, email: String, password: String): Flow<ResultState<CommonResponse>> = flow {
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

    fun login(email: String, password: String): Flow<ResultState<LoginResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val response = apiService.login(email, password)
            if (response.error == true) {
                emit(ResultState.Error(response.message.toString()))
            } else {
                response.loginResult?.let { loginResult ->
                    val user = UserModel(
                        loginResult.email ?: "",
                        loginResult.name ?: "",
                        loginResult.userId ?: "",
                        loginResult.token ?: "",
                        true
                    )
                    saveSession(user)
                }
                emit(ResultState.Success(response))
            }
        } catch (exc: HttpException) {
            val errorBody = exc.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson<LoginResponse>(errorBody, LoginResponse::class.java)
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
        if (keyword.isEmpty()) {
            return getAllDestinations()
        }
        return apiService.findDestination(keyword)
    }

    suspend fun getDestinationByCategory(category: String): DestinationResponse {
        return apiService.getDestinationByCategory(category)
    }

    suspend fun getAllFavorites(userId: String): DestinationResponse {
        return apiService.getAllFavorites(userId)
    }

    suspend fun isFavorite(userId: String, destinationId: String): Boolean {
        val response = getAllFavorites(userId)
        return response.data?.any { it.id == destinationId } ?: false
    }

    fun addFavorite(userId: String, destinationId: String): Flow<ResultState<CommonResponse>> = flow {
        try {
            val response = apiService.addFavorite(userId, destinationId)
            if (response.error == true) {
                emit(ResultState.Error(response.message.toString()))
            } else {
                emit(ResultState.Success(response))
            }
        } catch (exc: HttpException) {
            val errorBody = exc.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson<CommonResponse>(errorBody, CommonResponse::class.java)
            emit(ResultState.Error(errorResponse.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteFavorite(userId: String, destinationId: String): Flow<ResultState<CommonResponse>> = flow {
        try {
            val response = apiService.deleteFavorite(userId, destinationId)
            if (response.error == true) {
                emit(ResultState.Error(response.message.toString()))
            } else {
                emit(ResultState.Success(response))
            }
        } catch (exc: HttpException) {
            val errorBody = exc.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson<CommonResponse>(errorBody, CommonResponse::class.java)
            emit(ResultState.Error(errorResponse.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getNearestDestinations(latitude: Double, longitude: Double): DestinationResponse {
        val allDestinations = getAllDestinations().data ?: emptyList()

        // Calculate distances and sort by nearest
        val nearestDestinations = allDestinations.map { dataItem ->
            dataItem.let {
                val distance = calculateDistance(latitude, longitude, it.lat ?: 0.0, it.lon ?: 0.0)
                Pair(it, distance)
            }
        }.sortedBy { it.second } // Sort by distance ascending

        return DestinationResponse(
            data = nearestDestinations.map { it.first },
            error = false
        )
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radius = 6371 // Earth radius in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radius * c // Distance in kilometers
    }

    suspend fun getAllItineraries(userId: String): ItineraryResponse {
        return apiService.getAllItineraries(userId)
    }

    suspend fun generateItinerary(category: String, location: String): DestinationResponse {
        return apiService.generateItinerary(category, location)
    }

    suspend fun createItinerary(itinerary: Itinerary): CommonResponse {
        return apiService.createItinerary(itinerary)
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(apiService: ApiService, userPreference: UserPreference): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, userPreference)
            }.also { instance = it }
    }
}
