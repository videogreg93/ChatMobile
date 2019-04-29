package com.INF8405.chatmobile.models

import java.io.Serializable

data class Statistic(var time: Long, val bandwidth_download: Long, val bandwidth_upload: Long) : Serializable, Comparable<Statistic> {

    override fun compareTo(other: Statistic): Int {
        return (getMaxBandwidthValue() - other.getMaxBandwidthValue()).toInt()
    }

    fun getMaxBandwidthValue() : Long {
        return if(bandwidth_download > bandwidth_upload) bandwidth_download else bandwidth_upload
    }
}