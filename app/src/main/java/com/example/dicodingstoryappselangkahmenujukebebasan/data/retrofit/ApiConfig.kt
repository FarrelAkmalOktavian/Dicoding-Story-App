package com.example.dicodingstoryappselangkahmenujukebebasan.data.retrofit

import android.content.Context
import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.UserPreference
import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(context: Context): ApiService {
            val pref = UserPreference.getInstance(context.dataStore)
            val token = runBlocking { pref.fetchSession().first().token }

            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val authInterceptor = okhttp3.Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://story-api.dicoding.dev/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}


