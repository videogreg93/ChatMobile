package com.INF8405.chatmobile.system.managers

import com.INF8405.chatmobile.models.Profile

class ProfileManager(val firebaseManager: FirebaseManager) {
    lateinit var myId: String

    suspend fun getMyProfile(): Profile {
        return getProfile(myId)
    }

    suspend fun getProfile(id: String): Profile {
        return firebaseManager.getUser(id)
    }
}