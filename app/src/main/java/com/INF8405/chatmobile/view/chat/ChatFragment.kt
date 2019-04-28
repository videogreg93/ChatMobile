package com.INF8405.chatmobile.view.chat


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.INF8405.chatmobile.models.ChatMessage
import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.system.ChatMobileManagers
import com.INF8405.chatmobile.view.chat.adapter.ChatAdapter
import com.INF8405.chatmobile.view.profile.ProfileFragment
import kotlinx.android.synthetic.main.fragment_chat.*
import java.io.File
import java.io.IOException
import android.widget.Toast
import com.INF8405.chatmobile.R
import com.INF8405.chatmobile.service.FetchAddressIntentService
import com.INF8405.chatmobile.view.utils.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class ChatFragment : Fragment(), ChatContract.View {
    override lateinit var presenter: ChatContract.Presenter
    lateinit var friend: Profile
    lateinit var adapter: ChatAdapter
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location = Location("")
    private lateinit var resultReceiver: AddressResultReceiver
    private var currentAddress: String? = null
    private var imageDataToSend: ByteArray? = null
    private var sendingImage = false
    var picturesMap: HashMap<String, Bitmap> = HashMap()

    lateinit var photoURI: Uri


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ChatPresenter(this)

        // Init location service
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Init the result receiver
        resultReceiver = AddressResultReceiver(Handler())

        // Inflate the layout for this fragment
        return inflater.inflate(com.INF8405.chatmobile.R.layout.fragment_chat, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ChatAdapter(myId = ChatMobileManagers.profileManager.myId, fragment = this)
        messages.adapter = adapter
        messages.layoutManager = LinearLayoutManager(activity)
        preview_panel.visibility = View.GONE

        val profile = arguments?.getSerializable(PROFILE_ARG) as? Profile
        profile?.let {
            friend = it
            chat_title.text = "Chatting with ${friend.displayName}"
            chat_title.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(ProfileFragment.ID_ARG, friend.uid)
                ViewUtils.displayFragmentWithArgs(activity!!, ProfileFragment(), true, bundle)
            }
            presenter.connectToRoom(friend)
        }
        chat_input.setOnKeyListener { view, keyCode, event ->
            handleSendButton(view, keyCode, event)
        }

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
        autoScrollToEnd()
    }

    override fun onNewMessage(message: ChatMessage) {
        println("New Message")
        adapter.addItem(message)
        autoScrollToEnd()
    }

    private fun autoScrollToEnd() {
        activity?.let { activity ->
            val rv: RecyclerView = activity.findViewById(R.id.messages) as RecyclerView
            if (adapter.itemCount > 0)
                rv.smoothScrollToPosition(adapter.itemCount - 1)
        }
    }

    private fun handleSendButton(view: View, keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN &&
            keyCode == KeyEvent.KEYCODE_ENTER
        ) {
            val message = chat_input.text?.toString().orEmpty()
            if (sendingImage) {
                val imageName = "${requireActivity().getNewImageFileName()}_compressed"
                presenter.sendMessageWithImage(message, imageDataToSend!!, currentAddress, imageName, lastLocation, friend.uid)
                picturesMap.put(imageName, (preview_photo.drawable as BitmapDrawable).bitmap)
                clearPreviewImage()
            } else {
                presenter.sendMessage(message)
            }
            hideKeyboardFrom(view)
            chat_input.text?.clear()
            messages.invalidate()
        }
        return true
    }


    // Picture methods
    private fun takePicture() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(activity!!.packageManager) != null) {
            var pictureFile: File?
            try {
                pictureFile = activity?.createImageFile()
            } catch (ex: IOException) {
                Toast.makeText(activity, "Photo file can't be created, please try again", Toast.LENGTH_SHORT).show()
                return
            }

            if (pictureFile != null) {
                photoURI = FileProvider.getUriForFile(requireContext(), "com.INF8405.chatmobile", pictureFile)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //val originalBitmap = data?.extras?.get("data") as Bitmap
            val originalBitmap = MediaStore.Images.Media.getBitmap(activity?.getContentResolver(), photoURI);

            // Compress the data
            imageDataToSend = ImageUtils.compressImageToBitArray(originalBitmap, 50) ?: return

            val compressedBitmap = BitmapFactory.decodeByteArray(imageDataToSend, 0, imageDataToSend!!.size)

            // Set preview image
            preview_photo.setImageBitmap(compressedBitmap)

            // Request current address
            fetchCurrentAddress()

            // Set preview image visible
            setPicturePreview()
        }
    }

    private fun setPicturePreview() {
        preview_panel.visibility = View.VISIBLE
        sendingImage = true
    }

    private fun clearPreviewImage() {
        // Delete picture to send
        imageDataToSend = null
        preview_photo.setImageBitmap(null)
        preview_panel.visibility = View.GONE

        sendingImage = false
    }

    private fun startIntentService() {
        val intent = Intent(activity, FetchAddressIntentService::class.java).apply {
            putExtra(FetchAddressIntentService.Constants.RECEIVER, resultReceiver)
            putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, lastLocation)
        }
        requireActivity().startService(intent)
    }

    private fun fetchCurrentAddress() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("fetchCurrentAddress", "location permissions not set")
            Toast.makeText(context, "Could not access to user location", Toast.LENGTH_LONG).show()
            return
        }

        fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location ->
            lastLocation = location

            if (lastLocation == null) return@addOnSuccessListener

            if (!Geocoder.isPresent()) {
                Log.e("fetchCurrentAddress", "Geocoder not available")
                Toast.makeText(context, "Could not access to user location", Toast.LENGTH_LONG).show()
                return@addOnSuccessListener
            }

            Log.i("fetchCurrentAddress", "current location fetched")
            Toast.makeText(context, "Current address found !", Toast.LENGTH_LONG).show()

            // Start service and update UI to reflect new location
            startIntentService()
        }

    }


    internal inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {

        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            currentAddress = resultData?.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY) ?: ""

            // Show a toast message if an address was found.
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
                Toast.makeText(context, currentAddress, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Current address not found", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient?.flushLocations()
        picturesMap.clear()
        imageDataToSend = null
    }


    companion object {
        const val PROFILE_ARG = "profile"
        const val REQUEST_IMAGE_CAPTURE = 2
    }

}
