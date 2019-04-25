package com.INF8405.chatmobile.view.chat.adapter

import android.support.v7.util.DiffUtil
import com.INF8405.chatmobile.models.ChatMessage

class DiffUtil: DiffUtil.ItemCallback<ChatMessage>() {
    override fun areItemsTheSame(old: ChatMessage, new: ChatMessage): Boolean {
        return old.equals(new)
    }

    override fun areContentsTheSame(old: ChatMessage, new: ChatMessage): Boolean {
        return old.equals(new)
    }
}