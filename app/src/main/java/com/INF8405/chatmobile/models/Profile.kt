package com.INF8405.chatmobile.models

import com.google.firebase.auth.FirebaseUser

data class Profile(val uid: String, val displayName: String) {
    constructor(user: FirebaseUser) : this(user.uid, user.displayName.orEmpty())
}