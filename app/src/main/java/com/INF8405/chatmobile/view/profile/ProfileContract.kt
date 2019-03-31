package com.INF8405.chatmobile.view.profile

import android.content.Intent
import com.INF8405.chatmobile.view.base.BasePresenter
import com.INF8405.chatmobile.view.base.BaseView

/**
 * Contracts enable binding between a view and its presenter.
 */
interface ProfileContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter<View> {

    }
}