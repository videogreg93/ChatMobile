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

    override fun getStats() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveStats() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkPermission(context: Context): Boolean {
        // check usage access settings
        try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myPid(),
                ClassUtil.getPackageName(this.javaClass))

            if (mode == AppOpsManager.MODE_ALLOWED) {
                return true
            }
        } catch (e: Exception) {
        }

        return false
    }
}