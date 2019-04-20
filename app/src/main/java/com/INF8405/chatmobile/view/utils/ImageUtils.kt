package com.INF8405.chatmobile.view.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.support.media.ExifInterface
import java.io.ByteArrayInputStream
import android.app.Activity
import android.os.Environment
import android.provider.Settings
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*




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
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    val pictureFile: String = "CHATMOBILE" + androidId + timeStamp;
    val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val image: File = File.createTempFile(pictureFile, ".jpg", storageDir)
    ImageUtils.currentPhotoPath = image.absolutePath
    return image
}