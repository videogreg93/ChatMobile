package com.INF8405.chatmobile.view.signup

import com.INF8405.chatmobile.view.base.BasePresenter
import com.INF8405.chatmobile.view.base.BaseView

interface SignupContract {
    interface View : BaseView<Presenter> {
        fun doOnSignup()
        fun doOnError(t: Throwable?)
        fun setUsernameError(error: String?)
        fun setEmailError(error: String?)
        fun setPasswordError(error: String?)
    }

    interface Presenter : BasePresenter<View> {
        fun signUp(username: String, email: String, password: String)
        fun validate(username: String, email: String, password: String): Boolean
    }
}