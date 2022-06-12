package com.dicoding.picodiploma.tresh20.service



import com.dicoding.picodiploma.tresh20.response.LoginResp
import com.dicoding.picodiploma.tresh20.response.RegistResp
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("users")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confPassword") confPassword: String
    ) : Call<RegistResp>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResp>

}