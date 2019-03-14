package com.INF8405.chatmobile.view.base

/**
 * Views are tasked with only showing visual elements. Any data fetching, calculations, etc, should be
 * done in its presenter
 */
interface BaseView<T> {
     var presenter: T
}