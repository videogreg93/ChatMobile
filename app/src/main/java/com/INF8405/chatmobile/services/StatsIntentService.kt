package com.INF8405.chatmobile.services

import android.app.IntentService
import android.content.Intent
import android.net.TrafficStats
import android.util.Log
import java.util.*
import com.INF8405.chatmobile.models.Statistic

/*
* Service used to monitor some stats about the app and device, such as:
*   - App network's consumption
*   - App battery's consumption
* */
class StatsIntentService : IntentService(".system.services.stats.StatsIntentService") {
    private var uid = 0

    companion object {
        const val sampleNumberMax = 10 // 1000ms * 300 samples = 300 seconds sample
        const val sampleFrequency : Long = 1000 // sample each 500 ms
        val ACTION_BANDWIDTH = "send_bandwidth_usage"
        val PARAM_RESULT_BANDWIDTH_USAGE = "bandwidth_usage"
    }


    override fun onHandleIntent(workIntent: Intent) {
        val bandwidth_usage = ArrayDeque<Statistic>()
        var lastTotalBandwidth_record : Pair<Long, Long> = Pair(TrafficStats.getTotalRxBytes(),TrafficStats.getTotalTxBytes())
        uid = packageManager.getApplicationInfo(packageName, 0).uid

        while (true) {
            val currentTotal = Pair(TrafficStats.getUidRxBytes(uid), TrafficStats.getUidTxBytes(uid))
            val current = Statistic(0,
                currentTotal.first - lastTotalBandwidth_record.first,
                currentTotal.second - lastTotalBandwidth_record.second)
            lastTotalBandwidth_record = currentTotal

            if (bandwidth_usage.size >= sampleNumberMax)
                bandwidth_usage.removeFirst()
            bandwidth_usage.addLast(current)
            bandwidth_usage.map { statistic ->
                statistic.time += 1
            }

            // send data
            var intent = Intent()
            intent.action = ACTION_BANDWIDTH
            intent.putExtra(PARAM_RESULT_BANDWIDTH_USAGE, bandwidth_usage)
            sendBroadcast(intent)

            Thread.sleep(sampleFrequency)
        }
    }
}
