package com.INF8405.chatmobile.models

import java.io.Serializable

data class Statistic(var time: Long, val bandwidth_download: Long, val bandwidth_upload: Long) : Serializable