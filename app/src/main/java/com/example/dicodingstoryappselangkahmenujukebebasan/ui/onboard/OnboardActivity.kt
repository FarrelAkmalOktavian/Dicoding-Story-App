package com.example.dicodingstoryappselangkahmenujukebebasan.ui.onboard

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.main.MainActivity
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.main.MainViewModel
import com.example.dicodingstoryappselangkahmenujukebebasan.ViewModelFactory
import com.example.dicodingstoryappselangkahmenujukebebasan.databinding.ActivityOnboardBinding
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.login.LoginActivity
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.register.RegisterActivity

class OnboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardBinding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardBinding.inflate(layoutInflater)
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
        mainViewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.mainImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        binding.bottomCard.alpha = 0f

        val cardView = ObjectAnimator.ofFloat(binding.bottomCard, View.TRANSLATION_Y, 1000f, 0f).setDuration(500)
        val cardViewAlpha = ObjectAnimator.ofFloat(binding.bottomCard, View.ALPHA, 0f, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                AnimatorSet().apply {
                    playTogether(cardView, cardViewAlpha)
                }
            )
            startDelay = 500
            start()
        }
    }
}