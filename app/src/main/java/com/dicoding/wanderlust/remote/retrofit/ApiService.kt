package com.dicoding.wanderlust.remote.retrofit

import com.dicoding.wanderlust.remote.response.CommonResponse
import com.dicoding.wanderlust.remote.response.DestinationResponse
import com.dicoding.wanderlust.remote.response.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): CommonResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("destination")
    suspend fun getAllDestinations(): DestinationResponse

    @GET("search/{keyword}")
    suspend fun findDestination(
        @Path("keyword") keyword: String
    ): DestinationResponse

    @GET("destination/{category}")
    suspend fun getDestinationByCategory(
        @Path("category") category: String
    ): DestinationResponse

    @FormUrlEncoded
    @POST("addFavorit")
    suspend fun addFavorite(
        @Field("destinationId") destinationId: String
    ): CommonResponse

    @FormUrlEncoded
    @POST("deleteFavorit")
    suspend fun deleteFavorite(
        @Field("destinationId") destinationId: String
    ): CommonResponse

    @GET("favorit/{userId}")
    suspend fun getAllFavorites(
        @Path("userId") userId: String
    ): DestinationResponse

}