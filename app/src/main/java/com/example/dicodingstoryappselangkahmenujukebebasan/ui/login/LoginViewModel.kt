package com.example.dicodingstoryappselangkahmenujukebebasan.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstoryappselangkahmenujukebebasan.data.Result
import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.UserModel
import com.example.dicodingstoryappselangkahmenujukebebasan.data.repository.UserRepository
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        // Menggunakan viewModelScope untuk menjalankan coroutine
        viewModelScope.launch {
            // Mengumpulkan hasil login dari repository
            userRepository.login(email, password).collect { result ->
                // Memperbarui LiveData dengan hasil login
                _loginResult.value = result
            }
        }
    }

    fun saveSession(user: UserModel) {
        // Menyimpan session setelah login berhasil
        viewModelScope.launch {
            userRepository.storeSession(user)
        }
    }
}