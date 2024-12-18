package com.example.dicodingstoryappselangkahmenujukebebasan.ui.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.dicodingstoryappselangkahmenujukebebasan.databinding.ActivityDetailBinding
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.main.MainViewModel
import com.example.dicodingstoryappselangkahmenujukebebasan.ViewModelFactory
import com.example.dicodingstoryappselangkahmenujukebebasan.data.result.Result
import com.example.dicodingstoryappselangkahmenujukebebasan.di.Injection
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
    }

    private fun fetchStoryDetail(storyId: String) {
        lifecycleScope.launch {
            mainViewModel.getStoryDetail(storyId).observe(this@DetailActivity) { result ->
                when (result) {
                    is Result.Success -> {
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
                        // Tangani kesalahan jika diperlukan
                    }
                    Result.Loading -> {
                        // Tampilkan indikator loading
                    }
                }
            }
        }
    }

}
