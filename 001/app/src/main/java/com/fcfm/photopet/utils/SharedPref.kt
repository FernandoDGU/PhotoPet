package com.fcfm.photopet.utils

import android.content.Context
import android.net.wifi.hotspot2.pps.Credential

class SharedPref (val context: Context){

    val SHARED_NAME = "AUTOLOGIN"



    val managerPrefs =  context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)

    fun saveEmail(email: String){
        var editor =  managerPrefs.edit()
        editor.putString("email", email)
        editor.commit()
    }

    fun getEmail(): String? {
        return managerPrefs.getString("email","")
    }
}