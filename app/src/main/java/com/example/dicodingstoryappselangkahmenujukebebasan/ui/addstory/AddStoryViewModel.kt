package com.example.dicodingstoryappselangkahmenujukebebasan.ui.addstory

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.dicodingstoryappselangkahmenujukebebasan.data.repository.UserRepository
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.AddStoryResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.result.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {

    fun uploadStory(file: MultipartBody.Part, description: RequestBody): LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val result = repository.uploadStory(file, description)

            result.collect { response ->
                emit(response)
            }
        } catch (e: Exception) {
            emit(Result.Error("Terjadi kesalahan: ${e.message}"))
        }
    }
}