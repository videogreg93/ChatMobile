package com.INF8405.chatmobile.system.services.stats

import android.app.IntentService
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler;
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import com.INF8405.chatmobile.BuildConfig
import android.app.usage.NetworkStats
import android.os.Bundle
import java.util.*
import android.os.ResultReceiver

/*
* Service used to monitor some stats about the app and device, such as:
*   - App network's consumption
*   - App battery's consumption
* */
class StatsIntentService : IntentService("com.INF8405.chatmobile.system.services.stats.StatsIntentService") {

    private val handler = Handler()
    private lateinit var cm: ConnectivityManager
    private lateinit var nsm: NetworkStatsManager
    private lateinit var tm: TelephonyManager
    private var subscriberId: String? = ""
    private var bandwidthUsage: Deque<Pair<Long, Long>> = LinkedList<Pair<Long, Long>>()

    private enum class PARAM {
        RESULT_RECEIVER
    }

    override fun onCreate() {
        super.onCreate()

        cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        nsm = getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    override fun onHandleIntent(workIntent: Intent) {
        print("Stats service created")
        val resultReceiver: ResultReceiver = workIntent.getParcelableExtra(PARAM.RESULT_RECEIVER.name)

        while (true) {
            val current = getNetworkInfo(tm.networkType)
            if (bandwidthUsage.size >= 300) bandwidthUsage.removeFirst()
            bandwidthUsage.addLast(current)

            // send result every second
            var bundle = Bundle()
            bundle.putSerializable(StatsResultReceiver.PARAM_RESULT_DOWNLOAD, bandwidthUsage.last.first)
            bundle.putSerializable(StatsResultReceiver.PARAM_RESULT_UPLOAD, bandwidthUsage.last.second)
            resultReceiver.send(StatsResultReceiver.RESULT_CODE_OK, bundle)
        }
    }

    private fun getNetworkInfo(networkType: Int): Pair<Long, Long> {
        val networkStats = nsm.queryDetailsForUid(NetworkCapabilities.TRANSPORT_WIFI,
            getSubscriberId(networkType),
            0,
            System.currentTimeMillis(),
            android.os.Process.myPid())

        val bucket = NetworkStats.Bucket()
        networkStats.getNextBucket(bucket)

        return Pair(bucket.rxBytes / 1024, bucket.txBytes / 1024)
    }

    private fun getSubscriberId(networkType: Int): String {
        if(networkType == NetworkCapabilities.TRANSPORT_CELLULAR) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                return tm.subscriberId
            }
            // TO DO: handle permission denied
        }

        return "" // when connected via wi-fi
    }

    companion object {
        fun startService(context: Context, resultReceiverCallBack: StatsResultReceiver.ResultReceiverCallback<Int>) {
            val statsResultReceiver =
                StatsResultReceiver<Int>(Handler(context.mainLooper))
            statsResultReceiver.setReceiver(resultReceiverCallBack)

            val intent = Intent(context, StatsIntentService::class.java)
            intent.putExtra(PARAM.RESULT_RECEIVER.name, statsResultReceiver)
            context.startService(intent)
        }
    }
}
