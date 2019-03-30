package com.INF8405.chatmobile.view.chat.adapter

import android.support.v7.util.DiffUtil
import com.INF8405.chatmobile.models.ChatMessage

class DiffUtil: DiffUtil.ItemCallback<ChatMessage>() {
    override fun areItemsTheSame(p0: ChatMessage, p1: ChatMessage): Boolean {
        return false
    }

    override fun areContentsTheSame(p0: ChatMessage, p1: ChatMessage): Boolean {
        return false
    }
}