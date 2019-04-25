package com.INF8405.chatmobile.view.utils

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.INF8405.chatmobile.R
import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat.getSystemService
import android.view.View
import android.view.inputmethod.InputMethodManager


object ViewUtils {

    fun displayFragmentWithoutArgs(activity: FragmentActivity, fragmentToDisplay: Fragment, addToBackStack: Boolean) {

        val transaction = activity.supportFragmentManager
            .beginTransaction()
            .replace(com.INF8405.chatmobile.R.id.content, fragmentToDisplay, fragmentToDisplay.javaClass.name)
        if (addToBackStack) {
            transaction.addToBackStack(fragmentToDisplay.javaClass.name)
        }
        transaction.commit()
    }

    fun displayFragmentWithArgs(activity: FragmentActivity, fragmentToDisplay: Fragment, addToBackStack: Boolean, args: Bundle?) {
        fragmentToDisplay.arguments = args
        displayFragmentWithoutArgs(
            activity,
            fragmentToDisplay,
            addToBackStack
        )
    }


}

fun Fragment.hideKeyboardFrom(view: View) {
    val imm = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
    view.clearFocus();
}