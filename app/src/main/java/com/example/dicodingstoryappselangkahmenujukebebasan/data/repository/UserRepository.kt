package com.example.dicodingstoryappselangkahmenujukebebasan.data.repository

import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.UserModel
import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.UserPreference
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.AddStoryResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.LoginResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.RegisterResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.StoryDetailResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.StoryResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.result.Result
import com.example.dicodingstoryappselangkahmenujukebebasan.data.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody

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

    suspend fun getStories(page: Int, size: Int): Flow<Result<StoryResponse>> {
        return flow {
            try {
                val user = userPreference.fetchSession().firstOrNull()
                val token = user?.token.orEmpty()

                if (token.isEmpty()) {
                    emit(Result.Error("Token tidak ditemukan, silakan login kembali"))
                    return@flow
                }

                val response = apiService.getStories(page, size)
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error("Gagal mengambil cerita: ${e.message}"))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getStoryDetail(storyId: String): Flow<Result<StoryDetailResponse>> {
        return flow {
            try {
                val response = apiService.getStoryDetail(storyId)
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error("Error fetching story detail: ${e.message}"))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun uploadStory(
        file: MultipartBody.Part,
        descriptionPart: RequestBody
    ): Flow<Result<AddStoryResponse>> {
        return flow {
            emit(Result.Loading)
            try {
                val response = apiService.uploadStory(file, descriptionPart)
                if (response.error == false) {
                    emit(Result.Success(response))
                } else {
                    emit(Result.Error(response.message ?: "Upload failed"))
                }
            } catch (e: Exception) {
                emit(Result.Error("Gagal mengupload cerita: ${e.message}"))
            }
        }.flowOn(Dispatchers.IO)
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