package com.INF8405.chatmobile.models

import com.google.firebase.auth.FirebaseUser
import java.io.Serializable

data class Profile(val uid: String = "", val displayName: String = "") : Serializable {
    constructor(user: FirebaseUser) : this(user.uid, user.displayName.orEmpty())
}