package com.INF8405.chatmobile.view.chat

import android.location.Location
import com.INF8405.chatmobile.models.ChatMessage
import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.view.base.BasePresenter
import com.INF8405.chatmobile.view.base.BaseView

interface ChatContract {
    interface View : BaseView<Presenter> {
        fun onNewMessage(message: ChatMessage)
        abstract fun onGetHistoricMessages(oldMessages: List<ChatMessage>)
    }

    interface Presenter : BasePresenter<View> {
        fun connectToRoom(friend: Profile)
        fun sendMessage(message: String)
        fun sendMessageWithImage(message: String, imageData: ByteArray, currentAddress: String?, imageName: String, lastLocation: Location, friendId: String)
    }
}