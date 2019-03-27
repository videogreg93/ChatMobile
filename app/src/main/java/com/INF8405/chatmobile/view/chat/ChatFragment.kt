package com.INF8405.chatmobile.view.chat


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.system.ChatMobileManagers
import com.scaledrone.lib.Message
import com.scaledrone.lib.Room
import com.scaledrone.lib.RoomListener
import kotlinx.android.synthetic.main.fragment_chat.*


class ChatFragment : Fragment(), ChatContract.View {
    override lateinit var presenter: ChatContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       ChatPresenter(this)
        // Inflate the layout for this fragment
        return inflater.inflate(com.INF8405.chatmobile.R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profile = arguments?.getSerializable(PROFILE_ARG) as? Profile
        profile?.let {profile ->
            presenter.connectToRoom(profile)
        }
        chat_input.setOnKeyListener { view, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                 keyCode == KeyEvent.KEYCODE_ENTER) {
                presenter.sendMessage(chat_input.text?.toString().orEmpty())
            }
            true
        }

        // TODO setup recyclerview
    }

    override fun onNewMessage(message: String) {
        // TODO submit items to recyclerview
        println("Message: $message")
    }

    companion object {
        const val PROFILE_ARG = "profile"
    }

}
