package com.INF8405.chatmobile.view.chat.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.app.FragmentActivity
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.INF8405.chatmobile.R
import com.INF8405.chatmobile.models.ChatMessage
import com.INF8405.chatmobile.system.managers.FirebaseManager
import com.INF8405.chatmobile.view.chat.ChatFragment
import com.INF8405.chatmobile.view.utils.loadImageFile
import com.INF8405.chatmobile.view.utils.saveImageFile
import com.google.android.gms.common.ErrorDialogFragment
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_chat_bubble_left.view.*
import kotlinx.android.synthetic.main.item_chat_bubble_right.view.*

class ChatAdapter(var items: ArrayList<ChatMessage> = ArrayList(), val myId: String, val fragment: ChatFragment) :
    ListAdapter<ChatMessage, ChatAdapter.ViewHolder>(DiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (MESSAGE_TYPE.values()[viewType]) {
            MESSAGE_TYPE.RIGHT -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_bubble_right, parent, false)
            MESSAGE_TYPE.LEFT -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_bubble_left, parent, false)
        }
        return ViewHolder(view, viewType, fragment)

    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        if (message.ownerId.equals(myId)) {
            return MESSAGE_TYPE.RIGHT.ordinal
        } else {
            return MESSAGE_TYPE.LEFT.ordinal
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = getItem(position)
        when (MESSAGE_TYPE.values()[getItemViewType(position)]) {
            MESSAGE_TYPE.RIGHT -> holder.bindRight(message)
            MESSAGE_TYPE.LEFT -> holder.bindLeft(message)
        }
    }

    fun addItem(message: ChatMessage) {
        items.add(message)
        submitList(items)
    }

    fun addItems(messages: List<ChatMessage>) {
        items.addAll(messages)
        submitList(messages)
    }

    override fun getItem(position: Int): ChatMessage {
        return items.get(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    enum class MESSAGE_TYPE {
        RIGHT,
        LEFT
    }


    class ViewHolder(val view: View, val viewType: Int, val fragment: ChatFragment) : RecyclerView.ViewHolder(view) {
        fun bindLeft(message: ChatMessage) {
            populateView(message, view.chat_text_left, view.imageView_left, view.picture_address_left ,view.image_container_left)
        }

        fun bindRight(message: ChatMessage) {
            populateView(message, view.chat_text_right, view.imageView_right, view.picture_address_right, view.image_container_right)
        }

        private fun populateView(message: ChatMessage,textView: TextView, imageView: ImageView, addressView: TextView, imageContainer: LinearLayout) {
            textView.text = message.text
            if(message.picture != null)
            {
                val pictureName = message.picture!!.pictureId
                val fileRef = FirebaseManager.getFileReference(pictureName)

                if(fragment.picturesMap.containsKey(pictureName))
                {
                    val bmp = fragment.picturesMap.get(pictureName)!!
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.width, bmp.height, false))
                    Log.i("PopulateView", "Picture was loaded from the hashmap")
                }
                else{
                    try {
                        val bmp = fragment.requireActivity().loadImageFile(pictureName)
                        // Load the image directly from internal storage
                        if(bmp != null)
                        {
                            imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.width, bmp.height, false))
                            // Add the picture to the hashmap
                            fragment.picturesMap.put(pictureName, bmp)
                            Log.i("PopulateView", "Picture was loaded from local storage")
                        }
                        else {
                            // Save it in internal storage
                            loadImageFromFirebase(fragment.requireActivity(), pictureName, imageView, fileRef)
                            Log.i("PopulateView", "Picture was save in local storage and loaded from firebase")

                        }
                    } catch (ex: Exception) {
                        loadImageFromFirebase(imageView, fileRef)
                        Log.i("PopulateView", "Picture was loaded from firebase")
                    }
                }

                if(message?.picture?.place == null)
                {
                    addressView.visibility = View.GONE
                }
                else
                {
                    addressView.text = message?.picture?.place
                }

            } else {
                imageContainer.visibility = View.GONE
            }
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

        companion object {
            const val ONE_MEGABYTE: Long = 1024 * 2014
            const val FIVE_MEGABYTE: Long = ONE_MEGABYTE * 5
        }
    }
}