package com.INF8405.chatmobile.models

data class ChatMessage(var ownerId: String = "", var text: String = "") {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatMessage

        if (ownerId != other.ownerId) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ownerId.hashCode()
        result = 31 * result + text.hashCode()
        return result
    }
}