package com.example.dicodingstoryappselangkahmenujukebebasan.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.main.MainActivity
import com.example.dicodingstoryappselangkahmenujukebebasan.R
import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.UserModel
import com.example.dicodingstoryappselangkahmenujukebebasan.data.result.Result
import com.example.dicodingstoryappselangkahmenujukebebasan.databinding.ActivityLoginBinding
import com.example.dicodingstoryappselangkahmenujukebebasan.di.Injection
import kotlinx.coroutines.runBlocking

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels {
        val viewModelFactory = runBlocking { Injection.provideViewModelFactory(this@LoginActivity) }
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
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

            if (password.length < 8) {
                binding.passwordInput.error = "Password tidak boleh kurang dari 8 karakter"
                return@setOnClickListener
            }

            showLoading(true)

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

                        showLoading(false)

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
                        showLoading(false)
                        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        showLoading(false)
                        Toast.makeText(this, getString(R.string.login_error_message), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingIndicator.visibility = View.VISIBLE
            binding.loginButton.visibility = View.GONE
        } else {
            binding.loadingIndicator.visibility = View.GONE
            binding.loginButton.visibility = View.VISIBLE
        }
    }

    private fun playAnimation() {
        binding.cardViewLogin.alpha = 0f
        binding.loginText1.alpha = 0f
        binding.loginText2.alpha = 0f
        binding.emailLabel.alpha = 0f
        binding.emailInput.alpha = 0f
        binding.passwordLabel.alpha = 0f
        binding.passwordInput.alpha = 0f
        binding.loginButton.alpha = 0f

        val cardView = ObjectAnimator.ofFloat(binding.cardViewLogin, View.TRANSLATION_X, -1000f, 0f).setDuration(500)
        val cardViewAlpha = ObjectAnimator.ofFloat(binding.cardViewLogin, View.ALPHA, 0f, 1f).setDuration(500)

        val message =
            ObjectAnimator.ofFloat(binding.loginText1, View.TRANSLATION_X, -1000f, 0f).setDuration(500)
        val messageAlpha = ObjectAnimator.ofFloat(binding.loginText1, View.ALPHA, 0f, 1f).setDuration(500)

        val message2 =
            ObjectAnimator.ofFloat(binding.loginText2, View.TRANSLATION_X, -1000f, 0f).setDuration(250)
        val message2Alpha = ObjectAnimator.ofFloat(binding.loginText2, View.ALPHA, 0f, 1f).setDuration(250)

        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailLabel, View.TRANSLATION_X, -1000f, 0f).setDuration(250)
        val emailAlpha = ObjectAnimator.ofFloat(binding.emailLabel, View.ALPHA, 0f, 1f).setDuration(250)

        val emailTextInput =
            ObjectAnimator.ofFloat(binding.emailInput, View.TRANSLATION_X, -1000f, 0f).setDuration(250)
        val emailInputAlpha = ObjectAnimator.ofFloat(binding.emailInput, View.ALPHA, 0f, 1f).setDuration(250)

        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordLabel, View.TRANSLATION_X, -1000f, 0f).setDuration(250)
        val passwordAlpha = ObjectAnimator.ofFloat(binding.passwordLabel, View.ALPHA, 0f, 1f).setDuration(250)

        val passwordTextInput =
            ObjectAnimator.ofFloat(binding.passwordInput, View.TRANSLATION_X, -1000f, 0f).setDuration(250)
        val passwordInputAlpha = ObjectAnimator.ofFloat(binding.passwordInput, View.ALPHA, 0f, 1f).setDuration(250)

        val login =
            ObjectAnimator.ofFloat(binding.loginButton, View.TRANSLATION_X, -1000f, 0f).setDuration(250)
        val loginAlpha = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 0f, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                AnimatorSet().apply {
                    playTogether(message, messageAlpha, message2, message2Alpha)
                },
                AnimatorSet().apply {
                    playTogether(cardView, cardViewAlpha)
                },
                AnimatorSet().apply {
                    playTogether(emailTextView, emailAlpha)
                },
                AnimatorSet().apply {
                    playTogether(emailTextInput, emailInputAlpha)
                },
                AnimatorSet().apply {
                    playTogether(passwordTextView, passwordAlpha)
                },
                AnimatorSet().apply {
                    playTogether(passwordTextInput, passwordInputAlpha)
                },
                AnimatorSet().apply {
                    playTogether(login, loginAlpha)
                }
            )
            start()
        }
    }

}

