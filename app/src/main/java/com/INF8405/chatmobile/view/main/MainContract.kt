package com.INF8405.chatmobile.view.main

import com.INF8405.chatmobile.view.base.BasePresenter
import com.INF8405.chatmobile.view.base.BaseView

interface MainContract {
    interface View : BaseView<Presenter> {
        fun doOnLogout()
    }

    interface Presenter : BasePresenter<View> {
        fun logout()
    }
}