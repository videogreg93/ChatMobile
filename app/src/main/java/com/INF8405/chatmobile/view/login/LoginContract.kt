package com.INF8405.chatmobile.view.login

import com.INF8405.chatmobile.view.base.BasePresenter
import com.INF8405.chatmobile.view.base.BaseView

/**
 * Contracts enable binding between a view and its presenter.
 */
interface LoginContract {

    interface View : BaseView<Presenter> {
        fun doOnAuthentication()
        fun doOnError(t: Throwable?)
    }

    interface Presenter : BasePresenter<View> {
        fun authenticate(requestCode: Int)
    }
}