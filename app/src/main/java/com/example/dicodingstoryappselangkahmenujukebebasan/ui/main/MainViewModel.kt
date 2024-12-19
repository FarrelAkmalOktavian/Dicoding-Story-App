package com.example.dicodingstoryappselangkahmenujukebebasan.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.UserModel
import com.example.dicodingstoryappselangkahmenujukebebasan.data.repository.UserRepository
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.StoryDetailResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.StoryResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.result.Result
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.fetchSession().asLiveData()
    }

    suspend fun getStories(page: Int, size: Int): LiveData<Result<StoryResponse>> {
        return repository.getStories(page, size).asLiveData()
    }

    suspend fun getStoryDetail(storyId: String): LiveData<Result<StoryDetailResponse>> {
        return repository.getStoryDetail(storyId).asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
