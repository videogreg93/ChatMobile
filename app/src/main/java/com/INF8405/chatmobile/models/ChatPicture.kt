package com.INF8405.chatmobile.models


data class ChatPicture(var pictureId: String = "", var place: String? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatPicture

        if (pictureId != other.pictureId) return false
        if (place != other.place) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pictureId.hashCode()
        result = 31 * result + place.hashCode()
        return result
    }
}