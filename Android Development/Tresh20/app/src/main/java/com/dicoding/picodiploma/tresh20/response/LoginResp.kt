package com.dicoding.picodiploma.tresh20.response

import com.google.gson.annotations.SerializedName

data class LoginResp(

    @field:SerializedName("status")
    val status: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("token")
    val token : String
)

