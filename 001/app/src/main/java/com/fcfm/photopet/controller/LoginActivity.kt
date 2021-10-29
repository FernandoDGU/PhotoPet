package com.fcfm.photopet.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fcfm.photopet.R
import com.fcfm.photopet.model.User
import com.fcfm.photopet.utils.loggedUser
import com.fcfm.photopet.utils.retrofit.RestEngine
import com.fcfm.photopet.utils.retrofit.RetrofitMessage
import com.fcfm.photopet.utils.retrofit.ServiceUser
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                    validation()
                }
                R.id.SignUp ->{
                    showRegister()
                }

            }
        }
    }

    private fun validation(){
        if(editTextemail.text.toString().isEmpty()){
            EmailErr.visibility = View.VISIBLE;
            return
        }else{
            EmailErr.visibility = View.GONE;
        }
        if(editTextPass.text.toString().isEmpty()){
            PassErr.visibility = View.VISIBLE;
            return
        }else{
            PassErr.visibility = View.GONE;
        }
        loggingUser()
        //showHome()
    }

    private fun loggingUser(){
        val service: ServiceUser =  RestEngine.getRestEngine().create(ServiceUser::class.java)
        val email = editTextemail.text.toString();
        val pass = editTextPass.text.toString()
        val result: Call<User> = service.logInUser(email, pass)

        result.enqueue(object: Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity,t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                val item =  response.body()
                if(item!!.description.isNullOrEmpty()){
                    Toast.makeText(this@LoginActivity, "Compruebe los datos", Toast.LENGTH_SHORT).show()
                }else{
                    loggedUser.setUser(item)
                    showHome()
                }

            }
        })
    }

    private fun showHome(){
        val intent = Intent(this, FragmentsActivity::class.java)
        intent.putExtra("destiny", "register");
        startActivity(intent)
    }

    private fun showRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

}