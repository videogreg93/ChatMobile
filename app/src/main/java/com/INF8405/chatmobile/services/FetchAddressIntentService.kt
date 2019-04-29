/**
 * Code adapted from
 * https://developer.android.com/training/location/display-address
 */

package com.INF8405.chatmobile.services

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import com.INF8405.chatmobile.view.chat.ChatFragment
import java.io.IOException
import java.util.*

class FetchAddressIntentService(name: String? = "AddressLookup") : IntentService(name) {
    private var receiver: ResultReceiver? = null

    override fun onHandleIntent(intent: Intent?) {
        intent ?: return

        val geocoder = Geocoder(this, Locale.getDefault())
        val location = intent.getParcelableExtra<Location>(Constants.LOCATION_DATA_EXTRA)
        receiver = intent.getParcelableExtra<ChatFragment.AddressResultReceiver>(Constants.RECEIVER)
        var addresses: List<Address> = emptyList()

        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        }
        catch (e: IOException) {
            Log.e("AddressLookup", "catch network or other IO problems.", e)
        }
        catch(e: IllegalArgumentException) {
            Log.e("AddressLookup", "catch invalide lat/lng values.", e)
        }

        if(addresses.isEmpty())
        {
            Log.e("AddressLookup", "Address not found.")
            deliverResultToReceiver(Constants.FAILURE_RESULT, "failed")
        } else {
            val address = addresses[0]
            val addressFragments = with(address) {
                (0..maxAddressLineIndex).map {getAddressLine(it)}
            }
            val addressString = addressFragments.joinToString(separator = "\n")
            Log.i("AddressLookup", "Address found $addressString")
            deliverResultToReceiver(Constants.SUCCESS_RESULT, addressString)
        }


    }

    private fun deliverResultToReceiver(resultCode: Int, message: String) {
        val bundle = Bundle().apply { putString(Constants.RESULT_DATA_KEY, message) }
        receiver?.send(resultCode, bundle)
    }


    object Constants {
        const val SUCCESS_RESULT = 0
        const val FAILURE_RESULT = 1
        private const val PACKAGE_NAME = "com.INF8405.chatmobile.service"
        const val RECEIVER = "$PACKAGE_NAME.RECEIVER"
        const val RESULT_DATA_KEY = "$PACKAGE_NAME.RESULT_DATA_KEY"
        const val LOCATION_DATA_EXTRA = "$PACKAGE_NAME.LOCATION_DATA_EXTRA"
    }

}