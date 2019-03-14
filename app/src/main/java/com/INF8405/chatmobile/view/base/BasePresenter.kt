package com.INF8405.chatmobile.view.base

/**
 * Presenters respond to view actions (e.g buttons, data requests, etc.)
 */
interface BasePresenter<T> {
    var myView: T
}