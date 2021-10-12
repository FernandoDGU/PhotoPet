package com.fcfm.photopet.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.fcfm.photopet.CreatePostActivity
import com.fcfm.photopet.R

class Fragment_Inicio : Fragment(){
    private lateinit var rootView: View


    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.fragment_inicio, container, false)

        //Se manda a llamar el otro activity
        //btnFloat.setOnClickListener{
         //   val intent = Intent(this@Fragment_Inicio.context, CreatePostActivity::class.java)
          //  startActivity(intent)
        //}

        return rootView
    }



}

