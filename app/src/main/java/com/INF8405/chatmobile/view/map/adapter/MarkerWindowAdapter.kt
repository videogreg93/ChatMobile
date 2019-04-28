package com.INF8405.chatmobile.view.map.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.INF8405.chatmobile.R
import com.INF8405.chatmobile.models.MapMarker
import com.INF8405.chatmobile.system.managers.FirebaseManager
import com.INF8405.chatmobile.view.utils.ImageUtils.FIVE_MEGABYTE
import com.INF8405.chatmobile.view.utils.loadImageFile
import com.INF8405.chatmobile.view.utils.saveImageFile
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.marker_info_window.view.*

class MarkerWindowAdapter(private val fragmentActivity: FragmentActivity) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(marker: Marker?): View {
        var view = fragmentActivity.layoutInflater.inflate(R.layout.marker_info_window, null)
        var mapMaker = marker?.tag as MapMarker

        view.marker_title.text = mapMaker?.senderName
        view.marker_address.text = mapMaker?.address

        val fileRef = FirebaseManager.getFileReference(mapMaker.pictureId)
        try {
            val bmp = fragmentActivity.loadImageFile(mapMaker.pictureId)
            // Load the image directly from internal storage
            return if(bmp != null) {
                view.marker_image.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.width, bmp.height, false))
                Log.i("MarkerView", "Picture was loaded from local storage")
                view
            } else {
                // Save it in internal storage
                loadImageFromFirebase(fragmentActivity, mapMaker.pictureId, view.marker_image, fileRef)
                Log.i("MarkerView", "Picture was save in local storage and loaded from firebase")
                view
            }
        } catch (ex: Exception) {
            loadImageFromFirebase(view.marker_image, fileRef)
            Log.i("MarkerView", "Picture was loaded from firebase")
            return view
        }



    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }



    /**
     * Load the image from firebase, and display it
     */
    private fun loadImageFromFirebase(imageView: ImageView, fileRef: StorageReference) {
        fileRef.getBytes(FIVE_MEGABYTE).addOnSuccessListener { ByteArray ->
            val bmp: Bitmap = BitmapFactory.decodeByteArray(ByteArray, 0, ByteArray.size)
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.width, bmp.height, false))
        }
    }

    /**
     * Load the image from firebase, save it in internal storage and display it
     */
    private fun loadImageFromFirebase(fragmentActivity: FragmentActivity?, imageName: String, imageView: ImageView, fileRef: StorageReference) {
        fileRef.getBytes(FIVE_MEGABYTE).addOnSuccessListener { ByteArray ->
            val bmp: Bitmap = BitmapFactory.decodeByteArray(ByteArray, 0, ByteArray.size)
            fragmentActivity?.saveImageFile(imageName, bmp)
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.width, bmp.height, false))
        }
    }

}