package com.INF8405.chatmobile.view.profile

import android.content.Intent
import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.view.base.BasePresenter
import com.INF8405.chatmobile.view.base.BaseView

/**
 * Contracts enable binding between a view and its presenter.
 */
interface ProfileContract {

    interface View : BaseView<Presenter> {
        fun onGetProfile(profile: Profile)
    }

    interface Presenter : BasePresenter<View> {
        fun getProfile(profileId: String)
        fun saveProfile(profile: Profile)
    }
}