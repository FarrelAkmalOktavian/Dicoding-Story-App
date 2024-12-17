package com.example.dicodingstoryappselangkahmenujukebebasan.ui.register

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
import com.example.dicodingstoryappselangkahmenujukebebasan.R
import com.example.dicodingstoryappselangkahmenujukebebasan.databinding.ActivityRegisterBinding
import com.example.dicodingstoryappselangkahmenujukebebasan.data.Result
import com.example.dicodingstoryappselangkahmenujukebebasan.di.Injection
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels {
        Injection.provideViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()

        registerViewModel.registerResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    showSuccessDialog(result.data.message ?: "Registration successful")
                }

                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }

                else -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        getString(R.string.login_error_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
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
        binding.signupButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val passwordValid = (binding.passwordInput.text?.length ?: 0) >= 8

                if (!passwordValid) {
                    Toast.makeText(this, "Password tidak boleh kurang dari 8 karakter", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                registerViewModel.register(name, email, password)
            } else {
                Toast.makeText(this, "Tolong lengkapi data kamu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingIndicator.visibility = View.VISIBLE
            binding.signupButton.visibility = View.GONE
        } else {
            binding.loadingIndicator.visibility = View.GONE
            binding.signupButton.visibility = View.VISIBLE
        }
    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Registrasi Sukses")
            setMessage(message)
            setPositiveButton("Login") { _, _ ->
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
            create()
            show()
        }
    }

    private fun playAnimation() {
        binding.cardViewLogin.alpha = 0f
        binding.signupText1.alpha = 0f
        binding.signupText2.alpha = 0f
        binding.signupText3.alpha = 0f
        binding.emailLabel.alpha = 0f
        binding.emailInput.alpha = 0f
        binding.passwordLabel.alpha = 0f
        binding.passwordInput.alpha = 0f
        binding.signupButton.alpha = 0f

        val cardView = ObjectAnimator.ofFloat(binding.cardViewLogin, View.TRANSLATION_X, -1000f, 0f).setDuration(500)
        val cardViewAlpha = ObjectAnimator.ofFloat(binding.cardViewLogin, View.ALPHA, 0f, 1f).setDuration(500)

        val message =
            ObjectAnimator.ofFloat(binding.signupText1, View.TRANSLATION_X, -1000f, 0f).setDuration(500)
        val messageAlpha = ObjectAnimator.ofFloat(binding.signupText1, View.ALPHA, 0f, 1f).setDuration(500)

        val message2 =
            ObjectAnimator.ofFloat(binding.signupText2, View.TRANSLATION_X, -1000f, 0f).setDuration(250)
        val message2Alpha = ObjectAnimator.ofFloat(binding.signupText2, View.ALPHA, 0f, 1f).setDuration(250)

        val message3 =
            ObjectAnimator.ofFloat(binding.signupText3, View.TRANSLATION_X, -1000f, 0f).setDuration(250)
        val message3Alpha = ObjectAnimator.ofFloat(binding.signupText3, View.ALPHA, 0f, 1f).setDuration(250)

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
            ObjectAnimator.ofFloat(binding.signupButton, View.TRANSLATION_X, -1000f, 0f).setDuration(250)
        val loginAlpha = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 0f, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                AnimatorSet().apply {
                    playTogether(message, messageAlpha, message2, message2Alpha)
                },
                AnimatorSet().apply {
                    playTogether(message3, message3Alpha)
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