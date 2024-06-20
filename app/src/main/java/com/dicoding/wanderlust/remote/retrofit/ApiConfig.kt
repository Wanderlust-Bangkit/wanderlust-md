package com.dicoding.wanderlust.remote.retrofit

import androidx.datastore.core.IOException
import com.dicoding.wanderlust.BuildConfig
import com.dicoding.wanderlust.data.pref.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        private const val BASE_URL = BuildConfig.BASE_URL

        fun getApiService(userPreference: UserPreference): ApiService {
            val loggingInterceptor = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor { chain ->
                    val original = chain.request()
                    val user = runBlocking { userPreference.getSession().first() }

                    val requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer ${user.token}")

                    val request = requestBuilder.build()
                    val response = chain.proceed(request)

                    if (response.code == 401) {
                        // Token is not valid, clear session and redirect to login
                        runBlocking { userPreference.logout() }
                        throw IOException("Token expired, please login again")
                    }

                    response
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}

