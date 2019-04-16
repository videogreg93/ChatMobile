package com.INF8405.chatmobile.view.map

import android.content.Intent
import com.INF8405.chatmobile.view.base.BasePresenter
import com.INF8405.chatmobile.view.base.BaseView

interface MapContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter<View> {

    }
}