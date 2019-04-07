package com.INF8405.chatmobile.models

import com.google.firebase.auth.FirebaseUser
import java.io.Serializable

data class Profile(var uid: String = "",
                   var displayName: String = "",
                   var email: String = "",
                   var location: String = "") : Serializable {
    constructor(user: FirebaseUser) : this(user.uid, user.displayName.orEmpty(), user.email.orEmpty())
}