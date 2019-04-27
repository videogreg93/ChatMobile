package com.INF8405.chatmobile.view.map

import com.INF8405.chatmobile.system.ChatMobileManagers
import com.INF8405.chatmobile.system.managers.FirebaseManager
import com.INF8405.chatmobile.system.managers.ProfileManager

class  MapsPresenter(
    override var myView: MapContract.View,
    private val firebaseManager: FirebaseManager = ChatMobileManagers.firebaseManager,
    private val profileManager: ProfileManager = ChatMobileManagers.profileManager
) : MapContract.Presenter {

    init {
        myView.presenter = this
    }
}