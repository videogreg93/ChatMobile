package com.INF8405.chatmobile.system.managers

import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.system.utils.readValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FirebaseManager {
    private val db = FirebaseDatabase.getInstance()
    private val usersRef = db.getReference(USERS)
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun login() {
        // TODO implement login

    }

    fun addUser(user: Profile) {
        usersRef.child(user.uid).setValue(user)
    }

    suspend fun getUser(id: String): Profile {
        return usersRef.child(id).readValue<Profile>().second
    }

    companion object {
        const val USERS = "Users"
    }
}