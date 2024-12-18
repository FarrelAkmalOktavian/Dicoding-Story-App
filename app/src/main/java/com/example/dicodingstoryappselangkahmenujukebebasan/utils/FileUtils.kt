package com.example.dicodingstoryappselangkahmenujukebebasan.utils

import android.content.Context
import android.net.Uri
import java.io.File

object FileUtils {
    fun getFileFromUri(context: Context, uri: Uri): File {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open URI")
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        inputStream.use { inputStream.copyTo(tempFile.outputStream()) }
        return tempFile
    }
}