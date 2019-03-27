package com.INF8405.chatmobile.view.login

import android.content.Intent
import com.INF8405.chatmobile.view.base.BasePresenter
import com.INF8405.chatmobile.view.base.BaseView

/**
 * Contracts enable binding between a view and its presenter.
 */
interface LoginContract {

    interface View : BaseView<Presenter> {
        fun doOnAuthentication()
        fun doOnError(t: Throwable?)
        fun signUp()
    }

    interface Presenter : BasePresenter<View> {
        fun authenticate(requestCode: Int)
        fun logIn(email: String, password: String)
        fun getUserSignedIn()
        fun getUser(data: Intent?)
    }
}