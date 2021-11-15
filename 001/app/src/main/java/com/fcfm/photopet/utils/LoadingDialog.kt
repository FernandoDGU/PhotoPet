package com.fcfm.photopet.utils

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.fragment.app.Fragment
import com.fcfm.photopet.R



class LoadingDialog(){
    private lateinit var myDialog: AlertDialog

    private fun buildDialog(builder: AlertDialog.Builder, dialogView: View){
        builder.setView(dialogView)
        builder.setCancelable(false)
        myDialog = builder.create()
        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()
    }

    inner class ActivityLD(val mActivity: Activity) {

        fun startLoading(text : String = ""){
            val inflater = mActivity.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_loading, null)
            val builder = AlertDialog.Builder(mActivity)

            //dialogView.findViewById<TextView>(R.id.TV_PB_text).text= text

            buildDialog(builder, dialogView)
        }
        fun isDismiss(){
            myDialog.dismiss()
        }
    }

    inner class FragmentLD(val mFragment: Fragment) {
        fun startLoading(text: String = ""){
            val inflater = mFragment.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_loading, null)
            val builder = AlertDialog.Builder(mFragment.context)

            //dialogView.findViewById<TextView>(R.id.TV_PB_text).text = text

            buildDialog(builder, dialogView)
        }
        fun isDismiss(){
            myDialog.dismiss()
        }
    }

}

