package com.INF8405.chatmobile.view.signup

import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.system.ChatMobileManagers
import com.INF8405.chatmobile.system.managers.FirebaseManager
import com.INF8405.chatmobile.system.managers.ProfileManager
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class  SignupPresenter(
    override var myView: SignupContract.View,
    private val firebaseManager: FirebaseManager = ChatMobileManagers.firebaseManager,
    private val profileManager: ProfileManager = ChatMobileManagers.profileManager
) : SignupContract.Presenter {
    init {
        myView.presenter = this
    }


    override fun signUp(username: String, email: String, password: String) {
        val createTask = firebaseManager.mAuth.createUserWithEmailAndPassword(email, password)
        createTask.addOnCompleteListener{ resultTask ->
            if (resultTask.isSuccessful) {

                val user = firebaseManager.mAuth.currentUser!!
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()

                user.updateProfile(profileUpdates)
                    .addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            GlobalScope.launch {
                                // Add user to UserTable if not there
                                firebaseManager.addUser(Profile(user))
                                profileManager.myId = user.uid
                                withContext(Dispatchers.Main) {
                                    myView.doOnSignup()
                                }
                            }
                        } else {
                            myView.doOnError(resultTask.exception)
                        }
                    }
            } else {
                // If sign in fails, display a message to the user.
                myView.doOnError(resultTask.exception)
            }
        }
    }

    override fun validate(username: String, email: String, password: String): Boolean {
        var valid: Boolean = true
        if (username.isEmpty() || username.length < 3) {
            myView.setUsernameError("at least 3 characters")
            valid = false
        } else {
            myView.setUsernameError(null)
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            myView.setEmailError("enter a valid email address")
            valid = false;
        } else {
            myView.setEmailError(null)
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            myView.setPasswordError("between 4 and 10 alphanumeric characters")
            valid = false
        } else {
            myView.setPasswordError(null)
        }

        return valid
    }
}
