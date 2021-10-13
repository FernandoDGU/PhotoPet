package com.fcfm.photopet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity: AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        val btnLogin = findViewById(R.id.btnLoginIngresar) as Button
        val txtregister = findViewById<TextView>(R.id.SignUp)
        btnLogin.setOnClickListener(this)
        txtregister.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v!=null){
            when (v!!.id){
                R.id.btnLoginIngresar ->{
                    showHome()
                }
                R.id.SignUp ->{
                    showRegister()
                }

            }
        }
    }

    private fun showHome(){
        val intent = Intent(this, FragmentsActivity::class.java)
        startActivity(intent)
    }

    private fun showRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

}