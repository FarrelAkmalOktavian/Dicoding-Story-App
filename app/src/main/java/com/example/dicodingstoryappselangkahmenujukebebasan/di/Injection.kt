package com.example.dicodingstoryappselangkahmenujukebebasan.di

import android.content.Context
import com.example.dicodingstoryappselangkahmenujukebebasan.ViewModelFactory
import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.UserPreference
import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.dataStore
import com.example.dicodingstoryappselangkahmenujukebebasan.data.repository.UserRepository
import com.example.dicodingstoryappselangkahmenujukebebasan.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        //val token = runBlocking { pref.fetchSession().first().token }
        val apiService = ApiConfig.getApiService(context)
        return UserRepository.getInstance(apiService, pref)
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val repository = provideRepository(context)
        return ViewModelFactory(repository)
    }
}
