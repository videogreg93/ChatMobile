package com.INF8405.chatmobile.view.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.INF8405.chatmobile.R
import com.INF8405.chatmobile.view.home.HomeFragment
import com.INF8405.chatmobile.view.login.LoginActivity
import com.INF8405.chatmobile.view.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainContract.View {
    override lateinit var presenter: MainContract.Presenter

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
        MainPresenter(this)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        // Default view
        navigation.selectedItemId = R.id.navigation_home
        // Add action bar
        setSupportActionBar(findViewById(R.id.my_toolbar))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.logout_button -> {
            presenter.logout()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun doOnLogout() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        // TODO leave app when on main screen, else show main screen
        ViewUtils.displayFragmentWithoutArgs(this,HomeFragment(), false)
    }

}
