package com.INF8405.chatmobile.view.login

import android.content.Intent
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.system.ChatMobileManagers
import com.INF8405.chatmobile.system.managers.FirebaseManager
import com.INF8405.chatmobile.system.managers.ProfileManager
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class  LoginPresenter(
    override var myView: LoginContract.View,
    private val firebaseManager: FirebaseManager = ChatMobileManagers.firebaseManager,
    private val profileManager: ProfileManager = ChatMobileManagers.profileManager
) : LoginContract.Presenter {

    init {
        myView.presenter = this
    }

    override fun logIn(email: String, password: String) {
        firebaseManager.mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ resultTask->
                if (resultTask.isSuccessful) {
                    GlobalScope.launch {
                        val user = firebaseManager.mAuth.currentUser!!
                        // Add user to UserTable if not there
                        firebaseManager.addUser(Profile(user))
                        profileManager.myId = user.uid
                        withContext(Dispatchers.Main) {
                            myView.doOnAuthentication()
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    myView.doOnError(resultTask.exception)
                }
            }
    }

    override fun authenticate(requestCode: Int) {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(OAUTH_WEB_KEY)
            .requestEmail()
            .build()
        (myView as? AppCompatActivity)?.let { activity ->
            val mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(activity, signInIntent, requestCode, null)
        }
    }

    override fun getUserSignedIn() {
        GlobalScope.launch {
            firebaseManager.mAuth.currentUser?.let {user ->
                profileManager.myId = user.uid
                withContext(Dispatchers.Main) {
                    myView.doOnAuthentication()
                }
            }
        }
    }

    override fun getUser(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null);
            firebaseManager.mAuth.signInWithCredential(credential)
                .addOnCompleteListener { resultTask ->
                    if (resultTask.isSuccessful) {
                        GlobalScope.launch {
                            val user = firebaseManager.mAuth.currentUser!!
                            // Add user to UserTable if not there
                            firebaseManager.addUser(Profile(user))
                            profileManager.myId = user.uid
                            withContext(Dispatchers.Main) {
                                myView.doOnAuthentication()
                            }
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        myView.doOnError(resultTask.exception)
                    }
                }
        } catch (e: ApiException) {
            myView.doOnError(e)
        }
    }

    companion object {
        const val OAUTH_WEB_KEY = "203568988693-ificnl5ut0qkeft5cer6fpk0n2kfii4e.apps.googleusercontent.com"
    }

}