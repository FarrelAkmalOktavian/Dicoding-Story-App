package com.example.dicodingstoryappselangkahmenujukebebasan.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstoryappselangkahmenujukebebasan.data.result.Result
import com.example.dicodingstoryappselangkahmenujukebebasan.data.repository.UserRepository
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> = _registerResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            userRepository.register(name, email, password).collect { result ->
                _registerResult.value = result
            }
        }
    }
}
