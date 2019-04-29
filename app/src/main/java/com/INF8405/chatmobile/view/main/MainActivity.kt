/**
 * Adapted from
 * http://www.androidrey.com/android-location-settings-dialog-tutorial/
 */

package com.INF8405.chatmobile.view.main

import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.INF8405.chatmobile.R
import com.INF8405.chatmobile.system.ChatMobileManagers
import com.INF8405.chatmobile.view.home.HomeFragment
import com.INF8405.chatmobile.view.login.LoginActivity
import com.INF8405.chatmobile.view.profile.ProfileFragment
import com.INF8405.chatmobile.view.stats.StatsFragment
import com.INF8405.chatmobile.view.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsRequest
import android.widget.Toast
import com.INF8405.chatmobile.services.StatsIntentService
import com.google.android.gms.common.api.ResultCallback


class MainActivity : AppCompatActivity(), MainContract.View, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {

    override lateinit var presenter: MainContract.Presenter
    protected lateinit var googleApiClient: GoogleApiClient
    protected lateinit var locationRequest: LocationRequest

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        // TODO replace default implementation
        // Use ViewUtils functions to create new fragments for the views
        when (item.itemId) {
            R.id.navigation_home -> {
                ViewUtils.displayFragmentWithoutArgs(this, HomeFragment(), false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                val myId = ChatMobileManagers.profileManager.myId
                val bundle = Bundle()
                bundle.putString(ProfileFragment.ID_ARG, myId)
                ViewUtils.displayFragmentWithArgs(this, ProfileFragment(), false, bundle)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_stats -> {
                ViewUtils.displayFragmentWithoutArgs(this, StatsFragment(), false)
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

        // Request user permissions
        requestUserPermissions()

        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).build()

        googleApiClient.connect()
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 30 * 1000
        locationRequest.fastestInterval = 5 * 1000

        // start the stats service
        val intent = Intent(this, StatsIntentService::class.java)
        startService(intent)
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
        if (!supportFragmentManager.popBackStackImmediate())
            ViewUtils.displayFragmentWithoutArgs(this,HomeFragment(), false)
    }

    private fun requestUserPermissions() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){

            ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION
            )
        }
    }

    override protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode != RESULT_OK) {
                Toast.makeText(applicationContext, "GPS is not enabled", Toast.LENGTH_LONG).show()
                finish()
                // Restart app
                startActivity(Intent(this@MainActivity, MainActivity::class.java))
            }

        }
    }

    override fun onConnected(bundle: Bundle?) {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.SettingsApi.checkLocationSettings(
            googleApiClient,
            builder.build()
        )

        result.setResultCallback(this)
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResult(locationSettingsResult: LocationSettingsResult) {
        val status = locationSettingsResult.status
        when (status.statusCode) {
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                try {
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)

                } catch (e : Exception){
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(googleApiClient.isConnected) {
            googleApiClient.disconnect()
        }
        // stop service
        stopService(Intent(this, StatsIntentService::class.java))
    }

    companion object {
        const val REQUEST_LOCATION = 123
        const val REQUEST_CHECK_SETTINGS = 100
    }
}
