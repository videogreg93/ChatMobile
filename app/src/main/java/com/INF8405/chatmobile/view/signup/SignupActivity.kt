package com.INF8405.chatmobile.view.signup

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.INF8405.chatmobile.R
import com.INF8405.chatmobile.view.main.MainActivity
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity(), SignupContract.View {
    override lateinit var presenter: SignupContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        SignupPresenter(this)

        sign_up_button.setOnClickListener {
            if(presenter.validate(sign_up_username.text.toString(), sign_up_email.text.toString(), sign_up_password.text.toString()))
            {
                presenter.signUp(sign_up_username.text.toString(), sign_up_email.text.toString(), sign_up_password.text.toString())
            }
        }
    }

    override fun doOnSignup() {
        Toast.makeText(applicationContext, "Signing up", Toast.LENGTH_LONG).show()
        val intent = Intent(this@SignupActivity, MainActivity::class.java)
        startActivity(intent)
    }

    override fun doOnError(t: Throwable?) {
        Toast.makeText(applicationContext, "Could not sign up", Toast.LENGTH_LONG).show()
    }

    override fun setUsernameError(error: String?) {
        sign_up_username.error = error
    }

    override fun setEmailError(error: String?) {
        sign_up_email.error = error
    }

    override fun setPasswordError(error: String?) {
        sign_up_password.error = error
    }
}