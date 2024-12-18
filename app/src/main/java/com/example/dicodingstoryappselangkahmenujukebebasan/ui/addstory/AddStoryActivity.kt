package com.example.dicodingstoryappselangkahmenujukebebasan.ui.addstory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.dicodingstoryappselangkahmenujukebebasan.data.result.Result
import com.example.dicodingstoryappselangkahmenujukebebasan.databinding.ActivityAddStoryBinding
import com.example.dicodingstoryappselangkahmenujukebebasan.di.Injection
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.main.MainActivity
import com.example.dicodingstoryappselangkahmenujukebebasan.utils.FileUtils
import com.example.dicodingstoryappselangkahmenujukebebasan.utils.reduceFileImage
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var selectedImageUri: Uri? = null
    private lateinit var addStoryViewModel: AddStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val viewModelFactory = Injection.provideViewModelFactory(applicationContext)
            addStoryViewModel = ViewModelProvider(
                this@AddStoryActivity,
                viewModelFactory
            )[AddStoryViewModel::class.java]
        }

        binding.selectImageButton.setOnClickListener {
            selectImage()
        }

        binding.uploadStoryButton.setOnClickListener {
            uploadStory()
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            binding.previewImageView.setImageURI(uri)
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectImage() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun uploadStory() {
        val description = binding.storyDescriptionEditText.text.toString()
        if (selectedImageUri == null || description.isEmpty()) {
            Toast.makeText(this, "Lengkapi data sebelum mengirim!", Toast.LENGTH_SHORT).show()
            return
        }

        selectedImageUri?.let { uri ->
            // Panggil observeViewModel() hanya setelah imageUri dan description terisi
            observeViewModel(uri, description)
        }
    }

    private fun observeViewModel(uri: Uri, description: String) {
        // Observasi hasil upload story hanya setelah gambar dipilih dan deskripsi valid
        lifecycleScope.launch {
            try {
                val file = FileUtils.getFileFromUri(this@AddStoryActivity, uri)
                val compressedFile = reduceFileImage(file, this@AddStoryActivity)

                // Validasi ukuran file
                if (compressedFile.length() > 1 * 1024 * 1024) {
                    Toast.makeText(this@AddStoryActivity, "Ukuran gambar melebihi 1MB", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val mimeType = contentResolver.getType(uri)?.takeIf { it in listOf("image/jpeg", "image/png") }
                    ?: run {
                        Toast.makeText(this@AddStoryActivity, "Format gambar tidak didukung", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                val imagePart = MultipartBody.Part.createFormData(
                    "photo", compressedFile.name, compressedFile.asRequestBody(mimeType.toMediaType())
                )
                val descriptionPart = description.toRequestBody("text/plain".toMediaType())

                // Kirim ke repository
                addStoryViewModel.uploadStory(imagePart, descriptionPart).observe(this@AddStoryActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            Toast.makeText(this@AddStoryActivity, "Uploading...", Toast.LENGTH_SHORT).show()
                        }
                        is Result.Success -> {
                            Toast.makeText(this@AddStoryActivity, "Story berhasil diupload!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@AddStoryActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                            finish()
                        }
                        is Result.Error -> {
                            Toast.makeText(this@AddStoryActivity, "Gagal mengupload story", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddStoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
