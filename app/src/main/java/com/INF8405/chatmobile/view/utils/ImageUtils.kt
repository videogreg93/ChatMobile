package com.INF8405.chatmobile.view.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.support.media.ExifInterface
import android.app.Activity
import android.os.Environment
import android.provider.Settings
import android.util.Log
import java.lang.Exception
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.BitmapFactory
import java.io.*


object ImageUtils {

    var currentPhotoPath: String = ""


    fun getPortraitBitmap(bitmap: Bitmap): Bitmap {

        val byteSize = bitmap.getRowBytes() * bitmap.getHeight()
        val byteBuffer = ByteBuffer.allocate(byteSize)
        bitmap.copyPixelsToBuffer(byteBuffer)

// Get the byteArray.
        val byteArray = byteBuffer.array()

// Get the ByteArrayInputStream.
        val bs = ByteArrayInputStream(byteArray)

        val ei = ExifInterface(bs)
        val orientation = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        return when (orientation) {

            ExifInterface.ORIENTATION_UNDEFINED -> rotateImage(bitmap, 90f)

            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)

            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)

            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)

            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }
    }

    fun compressImageToBitArray(bitmap: Bitmap, quality: Int): ByteArray? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)

        return baos.toByteArray()
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }
}

@Throws(IOException::class)
fun Activity.createImageFile(): File {
    val pictureFile = getNewImageFileName()
    val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(pictureFile, ".jpg", storageDir)
}

fun Activity.getNewImageFileName(): String {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    return "CHATMOBILE$androidId$timeStamp"
}

fun Activity.saveImageFile(imageName: String, data: Bitmap) {
    // Create an image file name
    val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(storageDir.path, "$imageName.jpg")
    val fos = FileOutputStream(imageFile)

    try {
        data.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    } catch(e: Exception) {
        Log.e("ImageUtils", "Could not save image.")
    } finally {
        try {
            fos.close()
            Log.i("ImageUtils", "Image was saved successfully")

        } catch (e: IOException) {
            Log.e("ImageUtils", "Could not close file output stream.")
        }
    }
}

fun Activity.loadImageFile(imageName: String): Bitmap? {
    // Create an image file name
    val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(storageDir.path, "$imageName.jpg")

    return try {
        BitmapFactory.decodeStream(FileInputStream(imageFile))
    } catch (e: FileNotFoundException) {
        null
    }
}