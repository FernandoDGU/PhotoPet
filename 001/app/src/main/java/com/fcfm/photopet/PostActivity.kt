package com.fcfm.photopet

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity

class PostActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publicacion)
        supportActionBar?.hide()

        val toggle: ToggleButton = findViewById(R.id.toggleButtonLike)
        toggle.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                Toast.makeText(applicationContext, "Te gusta", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(applicationContext, "Ya no te gusta", Toast.LENGTH_SHORT).show()
            }

        }
    }
}