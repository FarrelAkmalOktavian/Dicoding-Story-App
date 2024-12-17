package com.example.dicodingstoryappselangkahmenujukebebasan.ui.onboard

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dicodingstoryappselangkahmenujukebebasan.MainActivity
import com.example.dicodingstoryappselangkahmenujukebebasan.MainViewModel
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
}