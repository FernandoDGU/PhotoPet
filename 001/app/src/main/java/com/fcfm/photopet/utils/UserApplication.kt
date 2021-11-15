package com.fcfm.photopet.utils

import android.app.Application

class UserApplication : Application() {
    companion object{
        lateinit var prefs:SharedPref
    }

    override fun onCreate() {
        super.onCreate()
        prefs = SharedPref(applicationContext)
    }
}