package com.INF8405.chatmobile.view.nfc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Parcelable
import android.content.Intent
import android.R
import com.INF8405.chatmobile.models.Profile
import kotlinx.android.synthetic.main.activity_nfc.*


class NFCActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.INF8405.chatmobile.R.layout.activity_nfc)
    }

    override fun onResume() {
        super.onResume()
        val intent = intent
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            val rawMessages = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES
            )

            val message = rawMessages[0] as NdefMessage // only one message transferred
            val profile = Profile.fromByteArray(message.records[0].payload)
            mTextView.setText(profile.displayName)

        } else
            mTextView.setText("Waiting for NDEF Message")

    }
}
