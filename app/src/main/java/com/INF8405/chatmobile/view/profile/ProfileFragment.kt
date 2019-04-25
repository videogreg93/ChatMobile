package com.INF8405.chatmobile.view.profile


import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.system.ChatMobileManagers
import kotlinx.android.synthetic.main.fragment_profile.*
import android.nfc.NdefRecord
import android.nfc.NdefMessage
import android.nfc.NfcEvent
import com.INF8405.chatmobile.R


class ProfileFragment : Fragment(), ProfileContract.View, NfcAdapter.CreateNdefMessageCallback {

    override lateinit var presenter: ProfileContract.Presenter
    private var currentlyEditing = false
    private lateinit var transparentDrawable: Drawable
    private lateinit var id: String

    // NFC Beam
    private lateinit var nfcAdapter: NfcAdapter
    // Flag to indicate that Android Beam is available
    private var androidBeamAvailable = false
    private lateinit var profile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ProfilePresenter(this)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transparentDrawable = display_name.background.current
        val profileId = arguments?.getString(ID_ARG)
        profileId?.let {
            id = profileId
            setupEditButton(profileId)
            presenter.getProfile(profileId)
            share_profile_button.setOnClickListener { nfcAdapter.invokeBeam(activity) }
        }
    }

    private fun setupEditButton(profileId: String) {
        val myId = ChatMobileManagers.profileManager.myId
        if (profileId.equals(myId)) {
            edit_button.isEnabled = true
            edit_button.visibility = View.VISIBLE
            edit_button.setOnClickListener {
                if (currentlyEditing) {
                    display_name.background = transparentDrawable
                    display_name.isEnabled = false
                    email.background = transparentDrawable
                    email.isEnabled = false
                    location.background = transparentDrawable
                    location.isEnabled = false
                    currentlyEditing = false
                    presenter.saveProfile(
                        Profile(
                            id,
                            display_name.text.toString(),
                            email.text.toString(),
                            location.text.toString()
                        )
                    )
                } else {
                    display_name.setBackgroundResource(android.R.drawable.edit_text)
                    display_name.isEnabled = true
                    email.setBackgroundResource(android.R.drawable.edit_text)
                    email.isEnabled = true
                    location.setBackgroundResource(android.R.drawable.edit_text)
                    location.isEnabled = true
                    currentlyEditing = true
                }
            }
        }
    }

    override fun onGetProfile(profile: Profile) {
        display_name.setText(profile.displayName)
        email.setText(profile.email)
        location.setText(profile.location)
        this.profile = profile
        setupNFC()
    }

    fun setupNFC() {
        activity?.packageManager?.let { packageManager ->
            androidBeamAvailable = if (!packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)) {
                // NFC isn't available on the device
                false
                // Android Beam file transfer isn't supported
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                false
            } else {
                // Android Beam file transfer is available, continue
                nfcAdapter = NfcAdapter.getDefaultAdapter(context)
                val fileUriCallback = FileUriCallback()
                // Set the dynamic callback for URI requests.
                // nfcAdapter.setNdefPushMessage(msg, activity)
                nfcAdapter.setNdefPushMessageCallback(this, activity);
                true
            }
        }
    }

    override fun createNdefMessage(event: NfcEvent?): NdefMessage {
        return NdefMessage(NdefRecord.createMime("text/plain", profile.toByteArray()))
    }

    companion object {
        const val ID_ARG = "profileId"
    }

}
