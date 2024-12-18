package com.example.dicodingstoryappselangkahmenujukebebasan.ui.addstory

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.dicodingstoryappselangkahmenujukebebasan.data.repository.UserRepository
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.AddStoryResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.result.Result
import com.example.dicodingstoryappselangkahmenujukebebasan.utils.FileUtils
import com.example.dicodingstoryappselangkahmenujukebebasan.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {
    // Fungsi untuk upload story
    fun uploadStory(file: MultipartBody.Part, description: RequestBody): LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            // Kirim data file dan description ke repository untuk upload
            val result = repository.uploadStory(file, description)

            result.collect { response ->
                emit(response)
            }
        } catch (e: Exception) {
            emit(Result.Error("Terjadi kesalahan: ${e.message}"))
        }
    }
}