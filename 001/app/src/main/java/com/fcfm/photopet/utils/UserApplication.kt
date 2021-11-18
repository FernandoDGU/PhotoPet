package com.fcfm.photopet.utils

import android.app.Application
import com.fcfm.photopet.utils.sqlite.DataDB

class UserApplication : Application() {
    companion object{
        lateinit var prefs:SharedPref
        lateinit var dbHelper: DataDB
    }

    override fun onCreate() {
        super.onCreate()
        prefs = SharedPref(applicationContext)
        dbHelper = DataDB(applicationContext)
    }
}