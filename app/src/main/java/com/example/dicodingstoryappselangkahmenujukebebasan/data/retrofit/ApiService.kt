package com.example.dicodingstoryappselangkahmenujukebebasan.data.retrofit

import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.LoginResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

}