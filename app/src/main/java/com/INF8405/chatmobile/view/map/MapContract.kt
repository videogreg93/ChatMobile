package com.INF8405.chatmobile.view.map

import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.INF8405.chatmobile.models.MapMarker
import com.INF8405.chatmobile.view.base.BasePresenter
import com.INF8405.chatmobile.view.base.BaseView
import com.google.android.gms.maps.GoogleMap

interface MapContract {

    interface View : BaseView<Presenter> {
        fun getFragmentActivity(): FragmentActivity?
    }

    interface Presenter : BasePresenter<View> {
        fun addMarkersToMap(map: GoogleMap)
    }
}