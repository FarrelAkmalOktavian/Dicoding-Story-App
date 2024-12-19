package com.example.dicodingstoryappselangkahmenujukebebasan.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.dicodingstoryappselangkahmenujukebebasan.data.repository.UserRepository
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.StoryResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.result.Result

class MapsViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getStoriesWithLocation(): LiveData<Result<StoryResponse>> {
        return liveData {
            emit(Result.Loading)
            emitSource(userRepository.getStoriesWithLocation().asLiveData())
        }
    }
}
