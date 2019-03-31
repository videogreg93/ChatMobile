package com.INF8405.chatmobile.view.chat

import com.INF8405.chatmobile.models.ChatMessage
import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.view.base.BasePresenter
import com.INF8405.chatmobile.view.base.BaseView

interface ChatContract {
    interface View : BaseView<Presenter> {
        fun onNewMessage(message: ChatMessage, isHistoric: Boolean = false)
        abstract fun onGetHistoricMessages(oldMessages: List<ChatMessage>)
    }

    interface Presenter : BasePresenter<View> {
        fun connectToRoom(friend: Profile)
        fun sendMessage(message: String)
    }
}