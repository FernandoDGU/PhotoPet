package com.fcfm.photopet.utils

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.Fragment
import com.fcfm.photopet.R

class LoadingDialogActivity(val mActivity: Activity) {
    private lateinit var isDialog: AlertDialog
    fun startLoading(text : String = ""){
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_loading, null)
        val builder = AlertDialog.Builder(mActivity)

        //dialogView.findViewById<TextView>(R.id.TV_PB_text).text= text

        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isDialog.show()
    }
    fun isDismiss(){
        isDialog.dismiss()
    }
}

class LoadingDialogFragment(val mFragment: Fragment) {
    private lateinit var isDialog: AlertDialog
    fun startLoading(text: String = ""){
        val inflater = mFragment.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_loading, null)
        val builder = AlertDialog.Builder(mFragment.context)

        //dialogView.findViewById<TextView>(R.id.TV_PB_text).text = text

        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isDialog.show()
    }
    fun isDismiss(){
        isDialog.dismiss()
    }
}