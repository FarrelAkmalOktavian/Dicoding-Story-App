package com.example.dicodingstoryappselangkahmenujukebebasan.utils

import android.content.Context
import id.zelory.compressor.Compressor
import java.io.File

suspend fun reduceFileImage(imageFile: File, context: Context): File {
    return Compressor.compress(context, imageFile)
}