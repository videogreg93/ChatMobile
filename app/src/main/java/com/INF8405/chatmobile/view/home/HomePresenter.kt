package com.INF8405.chatmobile.view.home

import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.system.ChatMobileManagers
import com.INF8405.chatmobile.system.managers.ProfileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomePresenter(
    override var myView: HomeContract.View,
    private val profileManager: ProfileManager = ChatMobileManagers.profileManager
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
        val tempProfiles = ArrayList<Profile>()
        tempProfiles.add(Profile("Gghn23h23", "John doe"))
        myView.onGetMyFriends(tempProfiles)
    }
}