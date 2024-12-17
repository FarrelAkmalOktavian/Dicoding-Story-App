package com.example.dicodingstoryappselangkahmenujukebebasan.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dicodingstoryappselangkahmenujukebebasan.MainActivity
import com.example.dicodingstoryappselangkahmenujukebebasan.R
import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.UserModel
import com.example.dicodingstoryappselangkahmenujukebebasan.data.Result
import com.example.dicodingstoryappselangkahmenujukebebasan.databinding.ActivityLoginBinding
import com.example.dicodingstoryappselangkahmenujukebebasan.di.Injection

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels {
        Injection.provideViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            loginViewModel.login(email, password)

            loginViewModel.loginResult.observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.loadingIndicator.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        val loginResponse = result.data
                        val token = loginResponse.loginResult?.token ?: ""
                        val user = UserModel(email, token, true)

                        loginViewModel.saveSession(user)

                        AlertDialog.Builder(this).apply {
                            setTitle("Nice!")
                            setMessage("Selamat! Anda berhasil login, yuk kita mulai bercerita")
                            setPositiveButton("Next") { _, _ ->
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                    is Result.Error -> {
                        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        Toast.makeText(this, getString(R.string.login_error_message), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

