package com.INF8405.chatmobile.models

data class ChatMessage(var ownerId: String = "", var text: String = "", var picture: ChatPicture? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatMessage

        if (ownerId != other.ownerId) return false
        if (text != other.text) return false
        if(picture != other.picture) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ownerId.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + picture.hashCode()
        return result
    }
}