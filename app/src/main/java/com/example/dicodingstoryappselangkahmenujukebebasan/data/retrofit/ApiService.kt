package com.example.dicodingstoryappselangkahmenujukebebasan.data.retrofit

import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.LoginResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.RegisterResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.StoryDetailResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.StoryResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("stories")
    suspend fun getStories(
        //@Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Path("id") storyId: String
    ): StoryDetailResponse
}