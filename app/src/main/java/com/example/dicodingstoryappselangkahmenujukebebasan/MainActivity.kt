package com.example.dicodingstoryappselangkahmenujukebebasan

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dicodingstoryappselangkahmenujukebebasan.databinding.ActivityMainBinding
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLogout()
        noBack()
    }

    private fun setupLogout() {
        binding.logoutButton.setOnClickListener {
            mainViewModel.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun noBack() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@MainActivity).apply {
                    setTitle("Mau Keluar")
                    setMessage("Yakin nih mau keluar dari aplikasi?")
                    setPositiveButton("Ya") { _, _ ->
                        finishAffinity()
                    }
                    setNegativeButton("Batal") { dialog, _ ->
                        dialog.dismiss()
                    }
                    create()
                    show()
                }
            }
        })
    }
}