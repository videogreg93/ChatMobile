package com.INF8405.chatmobile.system.managers

import com.INF8405.chatmobile.models.ChatMessage
import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.system.utils.pushValue
import com.INF8405.chatmobile.system.utils.readList
import com.INF8405.chatmobile.system.utils.readValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.scaledrone.lib.Message

class FirebaseManager {
    private val db = FirebaseDatabase.getInstance()
    private val usersRef = db.getReference(USERS)
    private val messagesRef = db.getReference(MESSAGES)
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun addUser(user: Profile) {
        try {
            usersRef.child(user.uid).readValue<Profile>()
        } catch (e: Exception) {
            usersRef.child(user.uid).setValue(user)
        }
    }

    fun updateUser(profile: Profile) {
        usersRef.child(profile.uid).setValue(profile)
    }

    suspend fun getUser(id: String): Profile {
        return usersRef.child(id).readValue<Profile>().second
    }

    suspend fun getAllUsers(): List<Profile> {
        return usersRef.orderByChild(DISPLAYNAME).readList<Profile>().map { it.second }
    }

    suspend fun saveMessage(roomId: String, message: ChatMessage) {
        messagesRef.child(roomId).pushValue(message)
    }

    suspend fun getMessages(roomId: String): List<ChatMessage> {
        return messagesRef.child(roomId).orderByKey().readList<ChatMessage>().map { it.second }
    }


    companion object {
        const val USERS = "Users"
        const val DISPLAYNAME = "displayName"
        const val MESSAGES = "Messages"
    }
}