package com.INF8405.chatmobile.view.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.INF8405.chatmobile.R
import com.INF8405.chatmobile.view.home.HomeFragment
import com.INF8405.chatmobile.view.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        // TODO replace default implementation
        // Use ViewUtils functions to create new fragments for the views
        when (item.itemId) {
            R.id.navigation_home -> {
                ViewUtils.displayFragmentWithoutArgs(this, HomeFragment(), false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        // Default view
        navigation.selectedItemId = R.id.navigation_home
    }
}
