package com.INF8405.chatmobile.view.home


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter

import com.INF8405.chatmobile.R
import com.INF8405.chatmobile.models.Profile
import com.INF8405.chatmobile.view.chat.ChatFragment
import com.INF8405.chatmobile.view.utils.ViewUtils
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(), HomeContract.View {
    override lateinit var presenter: HomeContract.Presenter
    lateinit var profileAdapter: ProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = HomePresenter(this)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getMyUser()
        presenter.getMyFriends()
    }

    override fun onGetMyUser(user: Profile) {
        title.text = user.displayName
    }

    override fun onGetMyFriends(friends: List<Profile>) {
        profileAdapter = ProfileAdapter(requireContext(), R.layout.item_friends_list, friends)
        friends_list.adapter = profileAdapter
        friends_list.setOnItemClickListener { parent, view, position, id ->
            val profile = profileAdapter.getItem(position)
            val bundle = Bundle()
            bundle.putSerializable(ChatFragment.PROFILE_ARG, profile)
            ViewUtils.displayFragmentWithArgs(activity!!, ChatFragment(), false, bundle)
        }
    }


}
