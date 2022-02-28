package com.fveloz.redditreader.adapters

import android.view.View

interface BasicUIObserver<T> {
    fun onElementClicked( objec: T)
    fun onShareClicked( objec: T, view: View)
    fun onApproachingEnd()
}