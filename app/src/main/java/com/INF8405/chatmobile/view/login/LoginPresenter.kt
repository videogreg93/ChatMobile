package com.INF8405.chatmobile.view.login

import android.provider.Contacts
import com.INF8405.chatmobile.system.ChatMobileManagers
import com.INF8405.chatmobile.system.managers.FirebaseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginPresenter(
    override var myView: LoginContract.View,
    val firebaseManager: FirebaseManager = ChatMobileManagers.firebaseManager
) : LoginContract.Presenter {

    init {
        myView.presenter = this
    }

    override fun authenticate(requestCode: Int) {
        // Coroutines enable easy asychronous tasks
        GlobalScope.launch {
            firebaseManager.login()
            withContext(Dispatchers.Main) {
                myView.doOnAuthentication()
            }
        }
    }

}