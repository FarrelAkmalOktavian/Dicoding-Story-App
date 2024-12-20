package com.example.dicodingstoryappselangkahmenujukebebasan.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.AddStoryResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.result.Result
import com.example.dicodingstoryappselangkahmenujukebebasan.databinding.ActivityAddStoryBinding
import com.example.dicodingstoryappselangkahmenujukebebasan.di.Injection
import com.example.dicodingstoryappselangkahmenujukebebasan.ui.main.MainActivity
import com.example.dicodingstoryappselangkahmenujukebebasan.utils.FileUtils
import com.example.dicodingstoryappselangkahmenujukebebasan.utils.reduceFileImage
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var selectedImageUri: Uri? = null
    private var capturedImageFile: File? = null
    private lateinit var addStoryViewModel: AddStoryViewModel

    private val launcherCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            capturedImageFile?.let { file ->
                selectedImageUri = Uri.fromFile(file)
                binding.previewImageView.setImageURI(selectedImageUri)
            }
        } else {
            Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedInstanceState?.getString("selected_image_uri")?.let { uriString ->
            selectedImageUri = Uri.parse(uriString)
            binding.previewImageView.setImageURI(selectedImageUri)
        }

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

        binding.captureImageButton.setOnClickListener {
            openCamera()
        }

        binding.uploadStoryButton.setOnClickListener {
            uploadStory()
        }

        backButton()
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
            return
        }

        val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir).apply {
            capturedImageFile = this
        }
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", tempFile)
        launcherCamera.launch(uri)
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
        val isLocationEnabled = binding.addLocationSwitch.isChecked  // Ambil status SwitchCompat

        if (selectedImageUri == null || description.isEmpty()) {
            Toast.makeText(this, "Lengkapi data sebelum mengirim!", Toast.LENGTH_SHORT).show()
            return
        }

        selectedImageUri?.let { uri ->
            observeViewModel(uri, description, isLocationEnabled)
        }
    }


    private fun observeViewModel(uri: Uri, description: String, isLocationEnabled: Boolean) {
        lifecycleScope.launch {
            try {
                val file = FileUtils.getFileFromUri(this@AddStoryActivity, uri)
                val compressedFile = reduceFileImage(file, this@AddStoryActivity)

                if (compressedFile.length() > 1 * 1024 * 1024) {
                    Toast.makeText(this@AddStoryActivity, "Ukuran gambar melebihi 1MB", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val mimeType = contentResolver.getType(uri) ?: "image/*"
                val imagePart = MultipartBody.Part.createFormData(
                    "photo", compressedFile.name, compressedFile.asRequestBody(mimeType.toMediaType())
                )
                val descriptionPart = description.toRequestBody("text/plain".toMediaType())

                if (isLocationEnabled) {
                    checkLocationPermission()
                } else {
                    addStoryViewModel.uploadStory(imagePart, descriptionPart, null, null).observe(this@AddStoryActivity) { result ->
                        handleUploadResult(result)
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(this@AddStoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleUploadResult(result: Result<AddStoryResponse>) {
        when (result) {
            is Result.Loading -> {
                binding.loadingIndicator.visibility = View.VISIBLE
                Toast.makeText(this@AddStoryActivity, "Uploading...", Toast.LENGTH_SHORT).show()
            }
            is Result.Success -> {
                binding.loadingIndicator.visibility = View.GONE
                Toast.makeText(this@AddStoryActivity, "Story berhasil diupload!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AddStoryActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                finish()
            }
            is Result.Error -> {
                binding.loadingIndicator.visibility = View.GONE
                Toast.makeText(this@AddStoryActivity, "Gagal mengupload story", Toast.LENGTH_SHORT).show()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectedImageUri?.let {
            outState.putString("selected_image_uri", it.toString())
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            getLocation()
        }
    }

    private fun getLocation() {
        // Memeriksa izin lokasi
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    val latPart = location?.latitude?.toString()?.toRequestBody("text/plain".toMediaType())
                    val lonPart = location?.longitude?.toString()?.toRequestBody("text/plain".toMediaType())
                    observeViewModelWithLocation(latPart, lonPart)
                } else {
                    Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun observeViewModelWithLocation(latPart: RequestBody?, lonPart: RequestBody?) {
        val description = binding.storyDescriptionEditText.text.toString()
        selectedImageUri?.let { uri ->
            lifecycleScope.launch {
                val file = FileUtils.getFileFromUri(this@AddStoryActivity, uri)
                val compressedFile = reduceFileImage(file, this@AddStoryActivity)

                val mimeType = contentResolver.getType(uri) ?: "image/*"
                val imagePart = MultipartBody.Part.createFormData(
                    "photo", compressedFile.name, compressedFile.asRequestBody(mimeType.toMediaType())
                )
                val descriptionPart = description.toRequestBody("text/plain".toMediaType())

                addStoryViewModel.uploadStory(imagePart, descriptionPart, latPart, lonPart).observe(this@AddStoryActivity) { result ->
                    handleUploadResult(result)
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
        private const val REQUEST_LOCATION_PERMISSION = 101
    }
}