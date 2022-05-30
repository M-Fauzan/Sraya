package com.dicoding.fauzan.sraya

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

fun createTempFile(context: Context): File {
    val tempFile = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(SimpleDateFormat("dd-MM-yyyy", Locale.US)
        .format(System.currentTimeMillis()), null, tempFile)
}
fun uriToFile(uri: Uri, context: Context): File {
    val contentResolver = context.contentResolver
    val tempFile = createTempFile(context)
    val inputStream = contentResolver.openInputStream(uri) as InputStream
    val outputStream = FileOutputStream(tempFile)
    val byteArray = ByteArray(1024)
    var len: Int
    while (inputStream.read(byteArray).also { len = it } > 0) {
        outputStream.write(byteArray, 0, len)
    }
    return tempFile
}