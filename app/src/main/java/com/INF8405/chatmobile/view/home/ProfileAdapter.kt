package com.INF8405.chatmobile.view.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.INF8405.chatmobile.models.Profile
import kotlinx.android.synthetic.main.item_friends_list.view.*

class ProfileAdapter(context: Context, val res: Int, items: List<Profile>) :
    ArrayAdapter<Profile>(context, res, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(res, parent, false)
        val profile = getItem(position)
        profile?.let {profile ->
            view.name.text = profile.displayName
        }

        return view

    }
}