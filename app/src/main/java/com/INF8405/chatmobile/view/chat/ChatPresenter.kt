package com.INF8405.chatmobile.view.chat

import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.system.ChatMobileManagers
import com.INF8405.chatmobile.system.managers.ScaledroneManager
import com.scaledrone.lib.Message
import com.scaledrone.lib.Room
import com.scaledrone.lib.RoomListener
import com.scaledrone.lib.Scaledrone

class ChatPresenter(override var myView: ChatContract.View,
                    private val scaledroneManager: ScaledroneManager = ChatMobileManagers.scaledroneManager
): ChatContract.Presenter {
    lateinit var drone: Scaledrone

    init {
        myView.presenter = this
    }

    override fun connectToRoom(friend: Profile) {
        // open chatroom with friend
        drone = scaledroneManager.drone
        // TODO create roomid based on unique string made my me and my friends uuid
        drone.subscribe("myroom", object : RoomListener {
            override fun onOpen(room: Room?) {}
            override fun onOpenFailure(room: Room?, ex: Exception?) {}

            override fun onMessage(room: Room?, message: Message?) {
                // parse as string
                message?.let {message ->
                    myView.onNewMessage(message.data.toString())
                }
            }
        })
    }

    override fun sendMessage(message: String) {
        drone.publish("myroom", message)
    }
}