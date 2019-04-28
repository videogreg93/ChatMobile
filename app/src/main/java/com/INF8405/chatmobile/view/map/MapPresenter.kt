package com.INF8405.chatmobile.view.map

import com.INF8405.chatmobile.models.MapMarker
import com.INF8405.chatmobile.system.ChatMobileManagers
import com.INF8405.chatmobile.system.managers.FirebaseManager
import com.INF8405.chatmobile.system.managers.ProfileManager
import com.INF8405.chatmobile.view.map.adapter.MarkerWindowAdapter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener



class MapPresenter(
    override var myView: MapContract.View,
    private val firebaseManager: FirebaseManager = ChatMobileManagers.firebaseManager,
    private val profileManager: ProfileManager = ChatMobileManagers.profileManager
) : MapContract.Presenter {

    init {
        myView.presenter = this
    }



    override fun addMarkersToMap(map: GoogleMap) {
        val dbRef = firebaseManager.getMarkersRef(profileManager.myId)

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (postSnapshot in dataSnapshot.children) {
                        val markerData: MapMarker = postSnapshot.getValue(MapMarker::class.java) as MapMarker

                        val fragmentActivity = myView.getFragmentActivity()
                        if(fragmentActivity != null) {
                            val mapMarkerView = MarkerWindowAdapter(fragmentActivity)
                            map.setInfoWindowAdapter(mapMarkerView)

                            val marker = map.addMarker(MarkerOptions().position(LatLng(markerData.latitude, markerData.longitude)).title(markerData.senderName))
                            marker.tag = markerData
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        })
    }
}