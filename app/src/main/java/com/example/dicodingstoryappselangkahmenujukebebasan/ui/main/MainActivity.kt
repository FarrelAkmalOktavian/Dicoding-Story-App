package com.example.dicodingstoryappselangkahmenujukebebasan.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingstoryappselangkahmenujukebebasan.StoryAdapter
import com.example.dicodingstoryappselangkahmenujukebebasan.data.result.Result
import com.example.dicodingstoryappselangkahmenujukebebasan.databinding.ActivityMainBinding
import com.example.dicodingstoryappselangkahmenujukebebasan.di.Injection
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.addstory.AddStoryActivity
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.detail.DetailActivity
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.login.LoginActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        val viewModelFactory = runBlocking { Injection.provideViewModelFactory(this@MainActivity) }
        viewModelFactory
    }
    private val storyAdapter: StoryAdapter by lazy {
        StoryAdapter(emptyList()) { storyId ->
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
        setupLogout()
        observeSession()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter.apply {
                onItemClick = { storyId ->
                    val intent = Intent(this@MainActivity, DetailActivity::class.java).apply {
                        putExtra("storyId", storyId) // Mengirimkan ID story
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun setupAddStory() {
        binding.addButton.setOnClickListener {
            //mainViewModel.logout()
            val intent = Intent(this, AddStoryActivity::class.java)
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
                lifecycleScope.launch {
                    fetchStories()
                }
            } else {
                println("Token is null or empty, cannot fetch stories")
                navigateToLogin()
            }
        }
    }


    private suspend fun fetchStories() {
        mainViewModel.getStories(1, 10).observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val filteredStories = result.data.listStory.filter {
                        it.name != null && it.photoUrl != null
                    }
                    storyAdapter.updateData(filteredStories)
                    showLoading(false)
                }
                is Result.Error -> {
                    println("Error fetching stories")
                    showLoading(false)

                }
                Result.Loading -> {
                    showLoading(true)
                }

                else -> {
                    println("Error fetching stories")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showErrorMessage(message: String) {
        binding.errorText.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
}