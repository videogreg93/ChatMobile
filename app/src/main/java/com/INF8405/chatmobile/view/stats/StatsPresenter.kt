package com.INF8405.chatmobile.view.stats

import android.app.AppOpsManager
import android.content.Context
import com.fasterxml.jackson.databind.util.ClassUtil

class StatsPresenter (
    override var myView: StatsContract.View
) : StatsContract.Presenter {

    init {
        myView.presenter = this
    }

}