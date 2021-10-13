package com.fcfm.photopet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity

class CreatePostActivity: AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crearpublicacion)
        supportActionBar?.hide()


        val btnPublicar = findViewById(R.id.btnPublicar) as Button
        btnPublicar.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if(v!=null){
            when (v!!.id){
                R.id.btnPublicar ->{
                    showPost()
                }
            }
        }
    }

    private fun showPost(){
        val intent = Intent(this, PostActivity::class.java)
        startActivity(intent)
    }
}