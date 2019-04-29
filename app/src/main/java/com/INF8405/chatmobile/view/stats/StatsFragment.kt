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
import com.INF8405.chatmobile.models.Statistic
import java.util.*
import com.INF8405.chatmobile.services.StatsIntentService
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


class StatsFragment: Fragment(),  StatsContract.View {
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
        graph.xAxis.axisMaximum = 10f

        // y axis
        graph.axisLeft.axisMinimum = 0f
        graph.axisRight.isEnabled = false
        graph.axisLeft.axisMaximum = 5000f

        // data
        var datasetDownload = LineDataSet(mutableListOf<Entry>(), "Bandwidth download usage")
        var datasetUpload = LineDataSet(mutableListOf<Entry>(), "Bandwidth upload usage")
        datasetDownload.color = Color.BLUE
        datasetUpload.color = Color.GREEN
        val lineData = LineData()
        lineData.addDataSet(datasetDownload)
        lineData.addDataSet(datasetUpload)
        graph.setTouchEnabled(false)
        graph.setPinchZoom(false)
        graph.data = lineData
        graph.description.text = "Bandwidth usage"
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

                bandwidth_download?.text = bandwidthUsage.last().bandwidth_download.toString()
                bandwidth_upload?.text = bandwidthUsage.last().bandwidth_upload.toString()
                val first = bandwidthUsage.first.time
                val listBandwidthDownload = bandwidthUsage.map {
                    Entry(it.time.toFloat() - first.toFloat(), it.bandwidth_download.toFloat())
                }
                val listBandWidthUpload = bandwidthUsage.map {
                    Entry(it.time.toFloat() - first.toFloat(), it.bandwidth_upload.toFloat())
                }
                val dataset_download = LineDataSet(listBandwidthDownload, "Bandwidth download")
                val dataset_upload = LineDataSet(listBandWidthUpload, "Bandwidth upload")
                dataset_download.color = Color.BLUE
                dataset_download.setDrawFilled(true)
                dataset_upload.color = Color.GREEN
                dataset_upload.setDrawFilled(true)
                if (graph != null) {
                    graph.data.removeDataSet(0)
                    graph.data.removeDataSet(1)
                    graph.data.addDataSet(dataset_download)
                    graph.data.addDataSet(dataset_upload)
                    graph.data.notifyDataChanged()
                    graph.notifyDataSetChanged()
                    graph.moveViewToX(bandwidthUsage.last.time.toFloat())
                    graph.invalidate()
                }

            }
        }
    }

    private class TimeValueFormatter : IAxisValueFormatter{

        private val mFormat: SimpleDateFormat

        init {
            mFormat = SimpleDateFormat("ss", Locale.CANADA_FRENCH) // use one decimal
        }

        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            val millis = TimeUnit.SECONDS.toMillis(value.toLong())
            return mFormat.format(Date(millis))
        }
    }
}