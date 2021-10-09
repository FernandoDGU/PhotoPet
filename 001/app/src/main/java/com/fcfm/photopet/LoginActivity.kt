package com.fcfm.photopet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity: AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        val btnLogin = findViewById(R.id.btnLoginIngresar) as Button

        btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v!=null){
            when (v!!.id){
                R.id.btnLoginIngresar ->{
                    showHome()
                }

            }
        }
    }

    private fun showHome(){
        val intent = Intent(this, FragmentsActivity::class.java)
        startActivity(intent)
    }

}