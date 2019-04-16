package com.INF8405.chatmobile.view.profile

import android.content.Intent
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.system.ChatMobileManagers
import com.INF8405.chatmobile.system.managers.FirebaseManager
import com.INF8405.chatmobile.system.managers.ProfileManager
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class  ProfilePresenter(
    override var myView: ProfileContract.View,
    private val firebaseManager: FirebaseManager = ChatMobileManagers.firebaseManager,
    private val profileManager: ProfileManager = ChatMobileManagers.profileManager
) : ProfileContract.Presenter {

    init {
        myView.presenter = this
    }

    override fun getProfile(profileId: String) {
        GlobalScope.launch {
            val profile = firebaseManager.getUser(profileId)
            withContext(Dispatchers.Main) {
                // clean up profile object
                if (profile.email.isBlank())
                    profile.email = "No email has been set"
                if (profile.location.isBlank())
                    profile.location = "No location has been set"
                myView.onGetProfile(profile)
            }
        }
    }

    override fun saveProfile(profile: Profile) {
        GlobalScope.launch {
            firebaseManager.updateUser(profile)
        }
    }
}