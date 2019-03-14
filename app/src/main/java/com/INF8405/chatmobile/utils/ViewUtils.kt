package com.INF8405.chatmobile.utils

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.INF8405.chatmobile.R

object ViewUtils {

    fun displayFragmentWithoutArgs(activity: FragmentActivity, fragmentToDisplay: Fragment, addToBackStack: Boolean) {

        val transaction = activity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, fragmentToDisplay, fragmentToDisplay.javaClass.name)
        if (addToBackStack) {
            transaction.addToBackStack(fragmentToDisplay.javaClass.name)
        }
        transaction.commit()
    }

    fun displayFragmentWithArgs(activity: FragmentActivity, fragmentToDisplay: Fragment, addToBackStack: Boolean, args: Bundle?) {
        fragmentToDisplay.arguments = args
        displayFragmentWithoutArgs(activity, fragmentToDisplay, addToBackStack)
    }
}