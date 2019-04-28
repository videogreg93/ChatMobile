package com.INF8405.chatmobile.view.map

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.INF8405.chatmobile.R
import com.INF8405.chatmobile.system.ChatMobileManagers.firebaseManager
import com.INF8405.chatmobile.system.ChatMobileManagers.profileManager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class MapFragment : Fragment(), OnMapReadyCallback, MapContract.View {
    override lateinit var presenter: MapContract.Presenter
    private lateinit var mMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        MapPresenter(this)

        val view = inflater.inflate(R.layout.fragment_map, container, false)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        (childFragmentManager.findFragmentById(R.id.fragment_map_view) as SupportMapFragment?)?.let {
            it.getMapAsync(this)
        }

        return view
    }

    /**
     * Manipulates the map once available.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val montreal = LatLng(45.50866990, -73.55399250)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(montreal, 10f))
        // Populate the map
        presenter.addMarkersToMap(mMap)
    }

    override fun getFragmentActivity(): FragmentActivity? {
        return activity
    }
}
