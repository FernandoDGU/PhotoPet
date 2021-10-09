package com.fcfm.photopet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button


class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val btnIngresar = findViewById(R.id.btnIngresar) as Button
        val btnRegistrar = findViewById(R.id.btnRegistrarse) as Button

        btnIngresar.setOnClickListener(this)
        btnRegistrar.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if(v!=null){
            when (v!!.id){
                R.id.btnIngresar ->{
                    showLogin()
                }
                R.id.btnRegistrarse ->{
                    showRegister()
                }

            }
        }
    }


    private fun showLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}