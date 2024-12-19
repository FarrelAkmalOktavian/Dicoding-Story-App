package com.example.dicodingstoryappselangkahmenujukebebasan.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.dicodingstoryappselangkahmenujukebebasan.databinding.ActivityDetailBinding
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.main.MainViewModel
import com.example.dicodingstoryappselangkahmenujukebebasan.data.result.Result
import com.example.dicodingstoryappselangkahmenujukebebasan.di.Injection
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.main.MainActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val mainViewModel: MainViewModel by viewModels {
        val viewModelFactory = runBlocking { Injection.provideViewModelFactory(this@DetailActivity) }
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra("storyId") ?: return

        fetchStoryDetail(storyId)

        backButton()
    }

    private fun fetchStoryDetail(storyId: String) {
        lifecycleScope.launch {
            mainViewModel.getStoryDetail(storyId).observe(this@DetailActivity) { result ->
                when (result) {
                    is Result.Success -> {
                        binding.loadingIndicator.visibility = View.GONE
                        val story = result.data.story
                        story?.let {
                            binding.detailName.text = it.name
                            binding.detailDescription.text = it.description
                            binding.detailCreatedAt.text = it.createdAt

                            Glide.with(this@DetailActivity)
                                .load(it.photoUrl)
                                .into(binding.detailImage)
                        }
                    }
                    is Result.Error -> {
                        binding.loadingIndicator.visibility = View.GONE
                        Toast.makeText(this@DetailActivity, "Gagal mendapatkan detail", Toast.LENGTH_SHORT).show()
                    }
                    Result.Loading -> {
                        binding.loadingIndicator.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun backButton() {
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

}
