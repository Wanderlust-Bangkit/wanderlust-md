package com.dicoding.wanderlust.data.model

data class UserModel(
    val email: String,
    val name: String,
    val userId: String,
    val token: String,
    val isLogin1: String,
    val isLogin: Boolean = false
)