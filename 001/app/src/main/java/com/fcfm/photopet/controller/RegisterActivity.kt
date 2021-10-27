package com.fcfm.photopet.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fcfm.photopet.R

class RegisterActivity: AppCompatActivity(), View.OnClickListener {
    lateinit var etName:EditText
    lateinit var etLast:EditText
    lateinit var etEmail:EditText
    lateinit var etPass:EditText
    lateinit var etDesc:EditText
    lateinit var etPhone:EditText


    lateinit var errorName:TextView
    lateinit var errorLast:TextView
    lateinit var errorEmail:TextView
    lateinit var errorPass:TextView
    lateinit var errorPhone:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        supportActionBar?.hide()


        val btnRegistro = findViewById(R.id.buttonRegister) as Button

        //EDIT TEXT VARIABLES
        etLast = findViewById(R.id.editTextLast) as EditText
        etEmail = findViewById(R.id.editTextEmailRegister) as EditText
        etPass = findViewById(R.id.editTextPasslRegister) as EditText
        etName = findViewById(R.id.editTextName) as EditText
        etPhone = findViewById(R.id.editTextPhoneRegister) as EditText

        //ERROR TEXT VARIABLES
        errorName = findViewById(R.id.NameError) as TextView
        errorLast = findViewById(R.id.LastError) as TextView
        errorEmail = findViewById(R.id.EmailError) as TextView
        errorPass = findViewById(R.id.PassError) as TextView
        errorPhone = findViewById(R.id.PhoneError) as TextView


        btnRegistro.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
            if(v!= null){
                when(v!!.id){
                    R.id.buttonRegister ->{
                        if(validate()){
                            showHome()
                        }else{
                            Toast.makeText(this, "Error, Por favor revise los datos", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }
    }

    private fun showHome(){
        val intent = Intent(this, FragmentsActivity::class.java)
        startActivity(intent)
    }

    private fun validateName():Boolean {
        if(etName.text.toString().isEmpty()){
            errorName.text = "Este campo no puede estar vacío"
            return false
        }
        errorName.text = ""
        return true
    }

    private fun validateLast():Boolean{
        if(etLast.text.toString().isEmpty()){
            errorLast.text = "Este campo no puede estar vacío"
            return false
        }
        errorLast.text = ""
        return true
    }

    private fun validateEmail():Boolean{
        if(etEmail.text.toString().isEmpty()){
            errorEmail.text = "Este campo no puede estar vacío"
            return false
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text).matches()){
            errorEmail.text = "Correo no valido"
            return false
        }
        errorEmail.text = ""
        return true
    }


    fun isValidPassword(password: String?) : Boolean {
        password?.let {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
            val passwordMatcher = Regex(passwordPattern)

            return passwordMatcher.find(password) != null
        } ?: return false
    }

    private fun validatePass():Boolean{
        if(etPass.text.toString().isEmpty()){
            errorPass.text = "Este campo no puede estar vacío"
            return false
        }else if (!isValidPassword(etPass.text.toString())){
            errorPass.text = "La contraseña debe de contener al menos:\nUn número \nUn carácter" +
                    "\nUna mayúscula y una minúscula"
            return false
        }else if(etPass.text.toString().length < 8){
            errorPass.text = "La contraseña debe ser mayor a 8 caracteres"
            return false
        }
        errorPass.text = ""
        return true
    }

    private fun validatePhone():Boolean{
        if(etPhone.text.toString().length < 10 && !etPhone.text.isEmpty()){
            errorPhone.text = "Deben de ser 10 numeros"
            return false
        }
        errorPhone.text = ""
        return true
    }

    private fun validate():Boolean{
        if(!validateName() || !validateLast() || !validateEmail() || !validatePass() || !validatePhone()){
            return false
        }

        return true
    }


}