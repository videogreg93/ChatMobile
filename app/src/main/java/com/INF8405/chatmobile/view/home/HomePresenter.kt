package com.INF8405.chatmobile.view.home

import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.system.ChatMobileManagers
import com.INF8405.chatmobile.system.managers.FirebaseManager
import com.INF8405.chatmobile.system.managers.ProfileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomePresenter(
    override var myView: HomeContract.View,
    private val profileManager: ProfileManager = ChatMobileManagers.profileManager,
    private val firebaseManager: FirebaseManager = ChatMobileManagers.firebaseManager
): HomeContract.Presenter {

    init {
        myView.presenter = this
    }

    override fun getMyUser() {
        GlobalScope.launch {
            val profile = profileManager.getMyProfile()
            withContext(Dispatchers.Main) {
                myView.onGetMyUser(profile)
            }
        }
    }

    override fun getMyFriends() {
        GlobalScope.launch {
            val profiles = firebaseManager.getAllUsers() as ArrayList
            profiles.removeAll { profile ->
                profile.uid.equals(profileManager.myId)
            }
            withContext(Dispatchers.Main) {
                myView.onGetMyFriends(profiles)
            }
        }
    }
}