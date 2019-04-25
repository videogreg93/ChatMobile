package com.INF8405.chatmobile.view.profile

import android.net.Uri
import android.nfc.NfcAdapter
import android.nfc.NfcEvent

class FileUriCallback : NfcAdapter.CreateBeamUrisCallback {
    private val fileUris = mutableListOf<Uri>()

    /**
     * Create content URIs as needed to share with another device
     */
    override fun createBeamUris(event: NfcEvent): Array<Uri> {
        return fileUris.toTypedArray()
    }

}

