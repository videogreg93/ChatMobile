package com.INF8405.chatmobile.system

import com.INF8405.chatmobile.system.managers.FirebaseManager
import com.INF8405.chatmobile.system.managers.ProfileManager

/**
 * Managers are singletons tasked with a specific job which shall be used
 * by multiple presenters (e.g. Authentication, Firebase, Messages, etc.)
 */
object ChatMobileManagers {
    var firebaseManager: FirebaseManager
    val profileManager: ProfileManager

    init {
        firebaseManager = FirebaseManager()
        profileManager = ProfileManager(firebaseManager)
    }
}