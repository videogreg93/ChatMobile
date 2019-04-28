package com.INF8405.chatmobile.view.stats

import android.content.Context
import com.INF8405.chatmobile.view.base.BasePresenter
import com.INF8405.chatmobile.view.base.BaseView

interface StatsContract {

    interface View : BaseView<Presenter> {
        fun onError()
    }

    interface Presenter : BasePresenter<View> {
        fun getStats()
        fun saveStats()
        fun checkPermission(context: Context): Boolean
    }
}