package com.INF8405.chatmobile.system.managers

import android.os.Debug
import android.util.Log
import com.scaledrone.lib.Listener
import com.scaledrone.lib.Scaledrone
import java.lang.Exception

class ScaledroneManager {
    val drone: Scaledrone

    init {
        drone = Scaledrone(channelId)
        drone.connect(object : Listener {
            override fun onOpen() {
                Log.d("Scaledrone", "Successful connected")
            }

            override fun onFailure(ex: Exception?) {
                Log.e("Scaledrone", "Could not connect")
                ex?.printStackTrace()
            }

            override fun onOpenFailure(ex: Exception?) {
                Log.e("Scaledrone", "Could not connect")
                ex?.printStackTrace()
            }

            override fun onClosed(reason: String?) {
                Log.d("Scaledrone", "Successful closed")
            }
        })
    }

    companion object {
        const val channelId = "8OG7jgx4czqPsc92"
    }
}