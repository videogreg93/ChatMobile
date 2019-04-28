package com.INF8405.chatmobile.view.chat

import android.location.Location
import android.util.Log


import com.INF8405.chatmobile.models.ChatMessage
import com.INF8405.chatmobile.models.ChatPicture
import com.INF8405.chatmobile.models.MapMarker
import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.system.ChatMobileManagers
import com.INF8405.chatmobile.system.managers.FirebaseManager
import com.INF8405.chatmobile.system.managers.ProfileManager
import com.INF8405.chatmobile.system.managers.ScaledroneManager
import com.fasterxml.jackson.databind.ObjectMapper

import com.scaledrone.lib.*
import kotlinx.coroutines.*

class ChatPresenter(
    override var myView: ChatContract.View,
    private val scaledroneManager: ScaledroneManager = ChatMobileManagers.scaledroneManager,
    private val profileManager: ProfileManager = ChatMobileManagers.profileManager,
    private val firebaseManager: FirebaseManager = ChatMobileManagers.firebaseManager
) : ChatContract.Presenter {
    lateinit var drone: Scaledrone
    lateinit var roomId: String


    init {
        myView.presenter = this
    }

    override fun connectToRoom(friend: Profile) {
        // open chatroom with friend
        drone = scaledroneManager.drone

        roomId = generateRoomId(friend)
        println("Connecting to room $roomId")
        getOldMessages(roomId)
        drone.subscribe(roomId, object : RoomListener {
            override fun onOpen(room: Room?) {}
            override fun onOpenFailure(room: Room?, ex: Exception?) {}

            override fun onMessage(room: Room?, message: Message?) {
                // parse as chatmessage
                message?.let { message ->
                    GlobalScope.launch {
                        val mapper = ObjectMapper()
                        val chatMessage = mapper.treeToValue(message.data, ChatMessage::class.java)
                        firebaseManager.saveMessage(roomId,chatMessage)
                        withContext(Dispatchers.Main) {
                            myView.onNewMessage(chatMessage)
                        }
                    }
                }
            }
        })

    }

    private fun getOldMessages(roomId: String) {
        GlobalScope.launch {
            val oldMessages = firebaseManager.getMessages(roomId)
            withContext(Dispatchers.Main) {
                myView.onGetHistoricMessages(oldMessages)
            }
        }
    }

    private fun generateRoomId(friend: Profile): String {
        val compare = profileManager.myId.compareTo(friend.uid)
        if (compare < 0)
            return (profileManager.myId + friend.uid)
        else
            return (friend.uid + profileManager.myId)
    }

    override fun sendMessage(message: String) {
            drone.publish(roomId, ChatMessage(profileManager.myId, message))
    }

    override fun sendMessageWithImage(
        message: String,
        imageData: ByteArray,
        currentAddress: String?,
        imageName: String,
        location: Location,
        friendId: String
    ) {
        // Send the picture to firebase
        GlobalScope.async {
            firebaseManager.savePictureFromBytes(imageName, imageData)
                .addOnFailureListener {
                    // Handle unsuccessful download
                    Log.i("upload", "image upload failed")
                }
                .addOnSuccessListener {
                    Log.i("upload", "image upload done")
                    val picture = ChatPicture(imageName, currentAddress)
                    // Send message
                    drone.publish(roomId, ChatMessage(profileManager.myId, message, picture))

                    // Add new marker to sender
                    val marker = MapMarker(senderName = profileManager.myName, pictureId = imageName, latitude = location.latitude, longitude = location.longitude, address = currentAddress)
                    GlobalScope.async {
                        firebaseManager.addMapMarkerToUser(profileManager.myId, marker)
                        firebaseManager.addMapMarkerToUser(friendId, marker)
                    }
                }
        }
    }
}