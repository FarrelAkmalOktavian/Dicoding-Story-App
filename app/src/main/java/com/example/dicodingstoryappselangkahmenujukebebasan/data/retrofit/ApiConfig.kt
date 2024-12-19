package com.example.dicodingstoryappselangkahmenujukebebasan.data.retrofit

import android.content.Context
import android.util.Log
import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.UserPreference
import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.dataStore
import kotlinx.coroutines.flow.first
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        suspend fun getApiService(context: Context): ApiService {
            val pref = UserPreference.getInstance(context.dataStore)
            val token = pref.fetchSession().first().token
            Log.d("Token", "Token fetched: $token")

            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val authInterceptor = Interceptor { chain ->
                val request = chain.request()

                val excludedEndpoints = listOf("/login", "/register")
                if (excludedEndpoints.any { request.url.encodedPath.contains(it) }) {
                    return@Interceptor chain.proceed(request)
                }

                if (token.isEmpty()) {
                    Log.e("Interceptor", "Token tidak ditemukan. Silakan login ulang.")
                    throw IllegalStateException("Token tidak ditemukan. Silakan login ulang.")
                }

                val authenticatedRequest = request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()

                chain.proceed(authenticatedRequest)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
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
