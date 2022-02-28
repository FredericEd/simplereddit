package com.fveloz.redditreader

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class ReaderApplication: Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null
        private var subreddit: String = "CrazyIdeas"
        fun getAppContext(): Context {
            return mContext!!
        }
        fun getSubredditName(): String {
            return subreddit
        }
    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
    }
}