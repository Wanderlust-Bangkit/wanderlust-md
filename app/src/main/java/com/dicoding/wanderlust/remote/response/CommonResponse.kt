package com.dicoding.wanderlust.remote.response

import com.google.gson.annotations.SerializedName

data class CommonResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
