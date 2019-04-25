package com.INF8405.chatmobile.view.main

import com.INF8405.chatmobile.system.ChatMobileManagers
import com.INF8405.chatmobile.system.managers.FirebaseManager
import com.INF8405.chatmobile.view.login.LoginContract

class MainPresenter(
    override var myView: MainContract.View,
    private val firebaseManager: FirebaseManager = ChatMobileManagers.firebaseManager
) : MainContract.Presenter {

    init {
        myView.presenter = this
    }

    override fun logout() {
        firebaseManager.mAuth.signOut()
        myView.doOnLogout()
    }
}