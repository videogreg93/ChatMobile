package com.INF8405.chatmobile.view.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.INF8405.chatmobile.R
import com.INF8405.chatmobile.view.main.MainActivity
import com.INF8405.chatmobile.view.signup.SignupActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), LoginContract.View {
    override lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        LoginPresenter(this)

        // Firebase automatically logs us in if we've logged in before
        presenter.getUserSignedIn()

        login_button.setOnClickListener{
            presenter.logIn(login_email.text.toString(), login_password.text.toString())
        }

        login_google_button.setOnClickListener {
            presenter.authenticate(REQUEST_CODE)
        }

        new_account_button.setOnClickListener {
            signUp()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_CODE -> {
                presenter.getUser(data)
            }
        }
    }

    override fun signUp() {
        val intent = Intent(this@LoginActivity, SignupActivity::class.java)
        startActivity(intent)
    }

    override fun doOnAuthentication() {
        Toast.makeText(applicationContext, "Logging in", Toast.LENGTH_LONG).show()
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
    }

    override fun doOnError(t: Throwable?) {
        Toast.makeText(applicationContext, "Could not login", Toast.LENGTH_LONG).show()
    }

    companion object {
        const val REQUEST_CODE = 301
    }
}
