package com.INF8405.chatmobile.models

data class MapMarker(
    var senderName: String = "",
    var pictureId: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var address: String? = null) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapMarker

        if (senderName != other.senderName) return false
        if (pictureId != other.pictureId) return false
        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false
        if (address != other.address) return false

        return true
    }

    override fun hashCode(): Int {
        var result = senderName.hashCode()
        result = 31 * result + pictureId.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + (address?.hashCode() ?: 0)
        return result
    }
}