package com.INF8405.chatmobile.view.chat


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.INF8405.chatmobile.models.ChatMessage
import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.system.ChatMobileManagers
import com.INF8405.chatmobile.view.chat.adapter.ChatAdapter
import com.INF8405.chatmobile.view.profile.ProfileFragment
import com.INF8405.chatmobile.view.utils.ImageUtils.getPortraitBitmap
import com.INF8405.chatmobile.view.utils.ViewUtils
import com.INF8405.chatmobile.view.utils.createImageFile
import kotlinx.android.synthetic.main.fragment_chat.*
import com.INF8405.chatmobile.view.utils.hideKeyboardFrom
import java.io.File
import java.io.IOException
import android.widget.Toast


class ChatFragment : Fragment(), ChatContract.View {
    override lateinit var presenter: ChatContract.Presenter
    lateinit var friend: Profile
    lateinit var adapter: ChatAdapter

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
        adapter = ChatAdapter(myId = ChatMobileManagers.profileManager.myId)
        messages.adapter = adapter
        messages.layoutManager = LinearLayoutManager(activity)

        val profile = arguments?.getSerializable(PROFILE_ARG) as? Profile
        profile?.let {
            friend = it
            chat_title.text = "Chatting with ${friend.displayName}"
            chat_title.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(ProfileFragment.ID_ARG, friend.uid)
                ViewUtils.displayFragmentWithArgs(activity!!,ProfileFragment(), true, bundle)
            }
            presenter.connectToRoom(friend)
        }
        chat_input.setOnKeyListener { view, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                presenter.sendMessage(chat_input.text?.toString().orEmpty())
                hideKeyboardFrom(view)
                chat_input.text?.clear()
                messages.invalidate()
            }
            true
        }

        preview_panel.visibility = View.GONE

        close_preview.setOnClickListener {
            clearPreviewImage()
        }

        // Setup picture button
        photo_button.setOnClickListener {
            takePicture()
        }
    }

    override fun onGetHistoricMessages(oldMessages: List<ChatMessage>) {
        adapter.addItems(oldMessages)
    }

    override fun onNewMessage(message: ChatMessage, isHistoric: Boolean) {
        if (isHistoric) {
            val items = adapter.items
            items.add(0, message)
            adapter.submitList(items)
        } else {
            adapter.addItem(message)
        }
    }

    override fun clearPreviewImage() {
        preview_photo.setImageBitmap(null)
        preview_panel.visibility = View.GONE

        // Tell the presenter to not send the picture with the message
        presenter.setSendingPicture(false)
    }


    // Picture methods
    private fun takePicture() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(activity!!.packageManager) != null) {
            var pictureFile: File?
            try {
                pictureFile = activity?.createImageFile()
            } catch (ex: IOException) {
                Toast.makeText(activity,"Photo file can't be created, please try again", Toast.LENGTH_SHORT).show()
                return
            }

            if (pictureFile != null) {
                val photoURI = FileProvider.getUriForFile(requireContext(),"com.INF8405.chatmobile", pictureFile)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val originalBitmap = data?.extras?.get("data") as Bitmap
            // Show preview image
            preview_photo.setImageBitmap(getPortraitBitmap(originalBitmap))
            preview_panel.visibility = View.VISIBLE

            // Tell the presenter to send the picture with the message
            presenter.setSendingPicture(true)
        }
    }


    companion object {
        const val PROFILE_ARG = "profile"
        const val REQUEST_IMAGE_CAPTURE = 2

    }

}
