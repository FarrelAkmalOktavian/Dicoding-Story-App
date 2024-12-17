package com.example.dicodingstoryappselangkahmenujukebebasan.data.repository

import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.UserModel
import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.UserPreference
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.LoginResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.retrofit.ApiService
import com.example.dicodingstoryappselangkahmenujukebebasan.data.Result
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.RegisterResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
     suspend fun login(email: String, password: String): Flow<Result<LoginResponse>> {
        return flow {
            emit(Result.Loading)
            try {
                val response = apiService.postLogin(email, password)
                if (response.error == false && response.loginResult != null) {
                    val loginResult = response.loginResult
                    val user = UserModel(
                        email = email,
                        token = loginResult.token ?: "",
                        isLogin = true
                    )
                    storeSession(user)
                    emit(Result.Success(response))
                } else {
                    emit(Result.Error(response.message ?: "Login failed"))
                }
            } catch (e: Exception) {
                emit(Result.Error("Login failed: ${e.message}"))
            }
        }
    }

    suspend fun register(name: String, email: String, password: String): Flow<Result<RegisterResponse>> {
        return flow {
            emit(Result.Loading)
            try {
                val response = apiService.register(name, email, password)
                if (response.error == false) {
                    emit(Result.Success(response))
                } else {
                    emit(Result.Error(response.message ?: "Registration failed"))
                }
            } catch (e: Exception) {
                emit(Result.Error("Registration failed: ${e.message}"))
            }
        }
    }


    suspend fun storeSession(user: UserModel) {
        userPreference.storeSession(user)
    }

    fun fetchSession(): Flow<UserModel> {
        return userPreference.fetchSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}