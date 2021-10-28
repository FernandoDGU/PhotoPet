package com.fcfm.photopet.controller

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fcfm.photopet.R
import com.fcfm.photopet.model.User
import com.fcfm.photopet.utils.loggedUser
import com.fcfm.photopet.utils.retrofit.RestEngine
import com.fcfm.photopet.utils.retrofit.RetrofitMessage
import com.fcfm.photopet.utils.retrofit.ServiceUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*

class RegisterActivity: AppCompatActivity(), View.OnClickListener {
    lateinit var etName:EditText
    lateinit var etLast:EditText
    lateinit var etEmail:EditText
    lateinit var etPass:EditText
    lateinit var etDesc:EditText
    lateinit var etPhone:EditText
    lateinit var Photo:ImageView


    lateinit var errorName:TextView
    lateinit var errorLast:TextView
    lateinit var errorEmail:TextView
    lateinit var errorPass:TextView
    lateinit var errorPhone:TextView

    var imgArray:ByteArray? =  null
    private val REQUEST_GALLERY = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        supportActionBar?.hide()


        val btnRegistro = findViewById(R.id.buttonRegister) as Button
        Photo = findViewById(R.id.RegisterImage) as ImageView

        //EDIT TEXT VARIABLES
        etLast = findViewById(R.id.editTextLast) as EditText
        etEmail = findViewById(R.id.editTextEmailRegister) as EditText
        etPass = findViewById(R.id.editTextPasslRegister) as EditText
        etName = findViewById(R.id.editTextName) as EditText
        etPhone = findViewById(R.id.editTextPhoneRegister) as EditText
        etDesc = findViewById(R.id.editTextDescRegister) as EditText

        //ERROR TEXT VARIABLES
        errorName = findViewById(R.id.NameError) as TextView
        errorLast = findViewById(R.id.LastError) as TextView
        errorEmail = findViewById(R.id.EmailError) as TextView
        errorPass = findViewById(R.id.PassError) as TextView
        errorPhone = findViewById(R.id.PhoneError) as TextView




        btnRegistro.setOnClickListener(this)
        Photo.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
            if(v!= null){
                when(v!!.id){
                    R.id.buttonRegister ->{
                        if(validate()){
                            createUser()
                        }else{
                            Toast.makeText(this, "Error, Por favor revise los datos", Toast.LENGTH_SHORT).show()
                        }

                    }
                    R.id.RegisterImage ->{
                        loadImage()
                    }
                }
            }
    }

    private fun createUser(){
        if(this.imgArray == null){
            val bitmap = (Photo!!.getDrawable() as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            this.imgArray = baos.toByteArray()
        }
        val encodedString: String =  Base64.getEncoder().encodeToString(this.imgArray)
        val strEncodeImage: String = "data:image/png;base64," + encodedString


        val user = User(
            etEmail.text.toString(),
            "${etName.text} ${etLast.text}",
            etPass.text.toString(),
            etPhone.text.toString(),
            etDesc.text.toString(),
            strEncodeImage
        );

        val service: ServiceUser =  RestEngine.getRestEngine().create(ServiceUser::class.java)
        val result: Call<RetrofitMessage> = service.insertUser(user)

        result.enqueue(object: Callback<RetrofitMessage> {
            override fun onFailure(call: Call<RetrofitMessage>, t: Throwable) {
                Toast.makeText(this@RegisterActivity,t.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<RetrofitMessage>, response: Response<RetrofitMessage>) {
                val item =  response.body()
                when(item!!.message){
                    "ok" -> {
                        loggedUser.setUser(user)
                        showHome()
                    }
                    "23000" -> {
                        Toast.makeText(this@RegisterActivity,R.string.Err23000,Toast.LENGTH_LONG).show()
                    }
                }

            }
        })
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
            errorPass.text = "La contraseña debe de contener al menos:\nUn número \nUn carácter especial: @#$%^&+=" +
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


    private fun loadImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY){
            //val photo = data?.extras?.get("data") as Bitmap
            val photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data!!.data)
            val stream = ByteArrayOutputStream()
            photo.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            imgArray =  stream.toByteArray()
            this.Photo!!.setImageBitmap(photo)
            //Photo.setImageURI(data!!.data)

        }
    }


}