package com.INF8405.chatmobile.system.services.stats

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import java.io.Serializable


class StatsResultReceiver<T>(handler: Handler?): ResultReceiver(handler) {

    private lateinit var receiverCallback: ResultReceiverCallback<T>

    companion object {
        val RESULT_CODE_OK = 1100
        val RESULT_CODE_ERROR = 555
        val PARAM_RESULT_UPLOAD = "upload"
        val PARAM_RESULT_DOWNLOAD = "download"
        val PARAM_EXCEPTION = "exception"
    }

    fun setReceiver(resultReceiverCallback: ResultReceiverCallback<T>) {
        receiverCallback = resultReceiverCallback
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        super.onReceiveResult(resultCode, resultData)

        if (resultCode == RESULT_CODE_OK) {
            receiverCallback.onSuccess(resultData!!.getSerializable(PARAM_RESULT_DOWNLOAD),
                resultData!!.getSerializable(PARAM_RESULT_UPLOAD))
        } else {
            receiverCallback.onError(resultData!!.getSerializable(PARAM_EXCEPTION) as Exception)
        }
    }

    interface ResultReceiverCallback<T> {
        fun onSuccess(download: Serializable, upload: Serializable)
        fun onError(e: Exception)
    }
}