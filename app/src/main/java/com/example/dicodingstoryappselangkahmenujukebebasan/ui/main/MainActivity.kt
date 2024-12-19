package com.example.dicodingstoryappselangkahmenujukebebasan.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingstoryappselangkahmenujukebebasan.StoryAdapter
import com.example.dicodingstoryappselangkahmenujukebebasan.databinding.ActivityMainBinding
import com.example.dicodingstoryappselangkahmenujukebebasan.di.Injection
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.addstory.AddStoryActivity
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.detail.DetailActivity
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.login.LoginActivity
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.map.MapsActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        val viewModelFactory = runBlocking { Injection.provideViewModelFactory(this@MainActivity) }
        viewModelFactory
    }
    private val storyAdapter: StoryAdapter by lazy {
        StoryAdapter { storyId ->
            val intent = Intent(this@MainActivity, DetailActivity::class.java).apply {
                putExtra("storyId", storyId)
            }
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupAddStory()
        setupMap()
        setupLogout()
        observeSession()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        })
    }

    private fun setupRecyclerView() {
        binding.recyclerViewStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter
        }

        lifecycleScope.launch {
            storyAdapter.loadStateFlow.collect { loadState ->
                binding.loadingIndicator.visibility = if (loadState.refresh is androidx.paging.LoadState.Loading) View.VISIBLE else View.GONE
                if (loadState.refresh is androidx.paging.LoadState.Error) {
                    Toast.makeText(this@MainActivity, "Gagal memuat cerita", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAddStory() {
        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setupMap() {
        binding.mapButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupLogout() {
        binding.logoutButton.setOnClickListener {
            mainViewModel.logout()
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    private fun observeSession() {
        mainViewModel.getSession().observe(this) { session ->
            val token = session.token
            if (token.isNotEmpty()) {
                observeStories()
            } else {
                navigateToLogin()
            }
        }
    }

    private fun observeStories() {
        lifecycleScope.launch {
            mainViewModel.getPagedStories().collectLatest { pagingData ->
                storyAdapter.submitData(pagingData)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showErrorMessage() {
        binding.loadingIndicator.visibility = View.VISIBLE
        Toast.makeText(this@MainActivity, "Gagal mendapatkan daftar Story", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Konfirmasi Keluar")
            setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
            setPositiveButton("Ya") { _, _ ->
                finishAffinity()
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

}