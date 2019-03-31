package com.INF8405.chatmobile.view.chat.adapter

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.INF8405.chatmobile.R
import com.INF8405.chatmobile.models.ChatMessage
import kotlinx.android.synthetic.main.item_chat_bubble_left.view.*
import kotlinx.android.synthetic.main.item_chat_bubble_right.view.*

class ChatAdapter(var items: ArrayList<ChatMessage> = ArrayList(), val myId: String) :
    ListAdapter<ChatMessage, ChatAdapter.ViewHolder>(DiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (MESSAGE_TYPE.values()[viewType]) {
            MESSAGE_TYPE.RIGHT -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_bubble_right, parent, false)
            MESSAGE_TYPE.LEFT -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_bubble_left, parent, false)
        }
        return ViewHolder(view, viewType)

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


    class ViewHolder(val view: View, val viewType: Int) : RecyclerView.ViewHolder(view) {
        fun bindLeft(message: ChatMessage) {
            view.chat_text_left.text = message.text
        }

        fun bindRight(message: ChatMessage) {
            view.chat_text_right.text = message.text
        }
    }
}