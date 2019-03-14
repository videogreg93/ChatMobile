package com.INF8405.chatmobile.view.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.INF8405.chatmobile.R
import com.INF8405.chatmobile.view.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), LoginContract.View {
    override lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        LoginPresenter(this)

        login_button.setOnClickListener {
            presenter.authenticate(AUTHENTICATION_CODE)
        }
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
        const val AUTHENTICATION_CODE = 301
    }
}
