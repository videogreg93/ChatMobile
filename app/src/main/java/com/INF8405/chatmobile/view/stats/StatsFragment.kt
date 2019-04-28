package com.INF8405.chatmobile.view.stats

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS
import android.content.Intent
import android.widget.Toast
import com.INF8405.chatmobile.system.services.stats.StatsResultReceiver
import java.io.Serializable
import java.lang.ref.WeakReference
import com.INF8405.chatmobile.R
import com.INF8405.chatmobile.system.services.stats.StatsIntentService
import kotlinx.android.synthetic.main.fragment_stats.*

class StatsFragment : Fragment(), StatsContract.View {
    override lateinit var presenter: StatsContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        StatsPresenter(this)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // check access to settings
        if (!presenter.checkPermission(requireContext())) {
            val intent = Intent(ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
        }
        //
        StatsIntentService.startService(requireContext(), TimedResultReceiver(this))
    }

    override fun onError() {
        Toast.makeText(context, "Stats are not available for your device", Toast.LENGTH_SHORT).show()
    }

    private inner class TimedResultReceiver(fragment: StatsFragment) : StatsResultReceiver.ResultReceiverCallback<Int> {
        private val fragmentRef: WeakReference<StatsFragment>?

        init {
            fragmentRef = WeakReference(fragment)
        }

        override fun onSuccess(download: Serializable, upload: Serializable) {
            fragmentRef?.get()?.let { statsFragment ->
                statsFragment.bandwidth_download?.text = "Download ${download.toString()} mb/s "
                statsFragment.bandwidth_upload?.text = "Upload ${upload.toString()} mb/s"
            }
        }

        override fun onError(e: Exception) {
            Toast.makeText(context, "Stats are not available for your device", Toast.LENGTH_SHORT).show()
        }
    }
}