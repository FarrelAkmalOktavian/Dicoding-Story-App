package com.example.dicodingstoryappselangkahmenujukebebasan.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.UserModel
import com.example.dicodingstoryappselangkahmenujukebebasan.data.repository.UserRepository
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.ListStoryItem
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.StoryDetailResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.fetchSession().asLiveData()
    }

    suspend fun getStoryDetail(storyId: String): LiveData<Result<StoryDetailResponse>> {
        return repository.getStoryDetail(storyId).asLiveData()
    }

    suspend fun getPagedStories(userToken: String): LiveData<PagingData<ListStoryItem>> {
        return repository.getPagedStories().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
