package com.INF8405.chatmobile.view.utils

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Matrix
import android.support.media.ExifInterface
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import android.R.array
import android.app.Activity
import android.os.Environment
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*


object ImageUtils {

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
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {
        // Save a file: path for use with ACTION_VIEW intents
        //currentPhotoPath = absolutePath
    }
}