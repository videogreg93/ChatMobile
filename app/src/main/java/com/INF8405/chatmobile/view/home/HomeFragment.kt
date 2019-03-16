package com.INF8405.chatmobile.view.home


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.INF8405.chatmobile.R
import com.INF8405.chatmobile.models.Profile
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(), HomeContract.View {
    override lateinit var presenter: HomeContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = HomePresenter(this)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onGetMyUser(user: Profile) {
        title.text = user.displayName
    }


}
