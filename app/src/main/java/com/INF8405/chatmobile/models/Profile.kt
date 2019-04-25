package com.INF8405.chatmobile.models

import com.google.firebase.auth.FirebaseUser
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable

data class Profile(var uid: String = "",
                   var displayName: String = "",
                   var email: String = "",
                   var location: String = "") : Serializable {

    fun toByteArray(): ByteArray {
        return SerializationUtils.serialize(this)
    }

    companion object {
        fun fromByteArray(data: ByteArray): Profile {
            return SerializationUtils.deserialize(data)
        }
    }

    constructor(user: FirebaseUser) : this(user.uid, user.displayName.orEmpty(), user.email.orEmpty())
}