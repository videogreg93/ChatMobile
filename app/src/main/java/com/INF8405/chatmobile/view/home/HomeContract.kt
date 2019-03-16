package com.INF8405.chatmobile.view.home

import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.view.base.BasePresenter
import com.INF8405.chatmobile.view.base.BaseView

interface HomeContract {

    interface View : BaseView<Presenter> {
        fun onGetMyUser(user: Profile)
    }

    interface Presenter : BasePresenter<View> {
        fun getMyUser()
    }

}