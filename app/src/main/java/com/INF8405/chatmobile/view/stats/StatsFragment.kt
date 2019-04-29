package com.INF8405.chatmobile.view.stats

import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.util.Log
import kotlinx.android.synthetic.main.fragment_stats.*
import android.content.IntentFilter
import android.graphics.Color
import android.widget.Toast
import com.INF8405.chatmobile.models.Statistic
import java.util.*
import com.INF8405.chatmobile.services.StatsIntentService
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class StatsFragment : Fragment(), StatsContract.View {
    override lateinit var presenter: StatsContract.Presenter
    private var networkStatsUpdateServiceReceiver: NetworkStatsUpdateServiceReceiver? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        StatsPresenter(this)
        // Inflate the layout for this fragment
        return inflater.inflate(com.INF8405.chatmobile.R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        networkStatsUpdateServiceReceiver = NetworkStatsUpdateServiceReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(StatsIntentService.ACTION_BANDWIDTH)

        context?.registerReceiver(networkStatsUpdateServiceReceiver, intentFilter)
        prepareGraph()
    }

    private fun prepareGraph() {
        // Axis
        // x axis
        graph.xAxis.position = XAxis.XAxisPosition.BOTTOM
        graph.xAxis.textSize = 12f
        graph.xAxis.setDrawGridLines(true)
        graph.xAxis.textColor = Color.BLACK
        //graph.xAxis.setValueFormatter(TimeValueFormatter())
        graph.xAxis.axisMinimum = 0f
        graph.xAxis.axisMaximum = StatsIntentService.sampleNumberMax.toFloat()

        // y axis
        graph.axisLeft.axisMinimum = 0f
        graph.axisRight.isEnabled = false
        graph.axisLeft.axisMaximum = 10000f // 5000 octets

        // graph
        graph.setTouchEnabled(true)
        graph.setPinchZoom(true)
        graph.description.text = "Bandwidth usage of the last 60 seconds"
        graph.invalidate()
    }

    override fun onPause() {
        super.onPause()

        context?.unregisterReceiver(networkStatsUpdateServiceReceiver)
    }

    override fun onResume() {
        super.onResume()

        if (networkStatsUpdateServiceReceiver == null)
            networkStatsUpdateServiceReceiver = NetworkStatsUpdateServiceReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(StatsIntentService.ACTION_BANDWIDTH)
        context?.registerReceiver(networkStatsUpdateServiceReceiver, intentFilter)
    }

    override fun onError() {
        Toast.makeText(context, "Stats are not available for your device", Toast.LENGTH_SHORT).show()
    }

    private fun statsMax(stats: Statistic) : Float {
        if (stats.bandwidth_download > stats.bandwidth_upload) {
            return stats.bandwidth_upload.toFloat()
        } else {
            return stats.bandwidth_upload.toFloat()
        }
    }

    private inner class NetworkStatsUpdateServiceReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == StatsIntentService.ACTION_BANDWIDTH) {
                var bandwidthUsage = ArrayDeque<Statistic>()

                try {
                    @Suppress("UNCHECKED_CAST")
                    bandwidthUsage =
                        intent.extras?.get(StatsIntentService.PARAM_RESULT_BANDWIDTH_USAGE) as ArrayDeque<Statistic>
                } catch (e: java.lang.Exception) {
                    Log.d("exception", "Could not retrieve bandwidth usage")
                }
                Log.d("broadcastReceiver", "receiving")

                // update textviews
                bandwidth_download?.text = bandwidthUsage.first().bandwidth_download.toString()
                bandwidth_upload?.text = bandwidthUsage.first().bandwidth_upload.toString()

                // update the graph
                val listBandwidthDownload = bandwidthUsage.map {
                    Entry(it.time.toFloat(), it.bandwidth_download.toFloat())
                }
                val listBandWidthUpload = bandwidthUsage.map {
                    Entry(it.time.toFloat(), it.bandwidth_upload.toFloat())
                }
                val dataset_download = LineDataSet(listBandwidthDownload, "Bandwidth download")
                val dataset_upload = LineDataSet(listBandWidthUpload, "Bandwidth upload")
                dataset_download.color = Color.BLUE
                dataset_download.setDrawFilled(true)
                dataset_upload.color = Color.GREEN
                dataset_upload.setDrawFilled(true)

                if (graph != null) {
                    if (graph.data != null) {
                        graph.data = null
                    }
                    val lineData = LineData()
                    lineData.addDataSet(dataset_download)
                    lineData.addDataSet(dataset_upload)
                    graph.data = lineData
                    graph.data.notifyDataChanged()
                    graph.notifyDataSetChanged()
                    val max = bandwidthUsage.max()?.getMaxBandwidthValue()?.toFloat()
                    if (max != null) {
                        graph.axisLeft.mAxisMaximum = max!!
                    }
                    graph.invalidate()
                }
            }
        }
    }
}